import bpy
import bmesh
import os
import struct


def save(operator, context, filename=""):
    # Get all existing materials
    # Get their textures
    # TODO check and validate filename

    # TODO check if all requirments are met
    # Go through all models
    models = {}

    for o in bpy.data.objects:
        if o.type != 'MESH':
            continue  # Skip it if it's not a mesh
        print("Exporting object: " + o.name)
        models[o.name] = {}  # Each model is a dict
        # Python note: http://stackoverflow.com/questions/2465921/how-to-copy-a-dictionary-and-only-edit-the-copy
        # Since we want to mutate it anyway no need to copy it back and forth, side by side, listen to me
        model = models[o.name]
        mesh = o.data
        # Get texture name, blender.stackexchange.com/questions/5121/find-the-name-of-textures-linked-to-an-object-in-python
        # TODO make this simpler and handle errors
        for mat_slot in o.material_slots:
            for mtex_slot in mat_slot.material.texture_slots:
                if mtex_slot:
                    if hasattr(mtex_slot.texture, 'image'):
                        model['texture_name'] = os.path.splitext(mtex_slot.texture.image.name)[0]

        try:
            print("Texture name: " + model['texture_name'])
        except KeyError:
            print("Missing texture for object")
            raise KeyError  # Not that smart

        # Recalculate normals TODO like this?
        mesh.calc_normals_split()  # Calculate split vertex normals, which preserve sharp edges TODO does this change anything?
        # Since we changed the mesh, we should validate it TODO is this necessary?
        mesh.update()
        mesh.validate()
        # Get bmesh for detailed data
        bmesh_mesh = bmesh.new()
        bmesh_mesh.from_mesh(mesh)
        # Set name
        model['element_name'] = o.name
        model['shape_name'] = mesh.name
        # Set world matrix
        model['world_matrix'] = get_mat4(o.matrix_world)
        # TODO the world matrix does not handle very small values
        # Check if model is visible
        model['is_visible'] = 0 if o.hide else 1
        # Iterate through each of their actions and make animations out of it TODO (sgs or in sga?)
        model['animations'] = []
        # Iterate through each of their vertices and grab all the data ==> Be aware that IB magic has to be applied
        model['vertex_data'] = {}
        model['vertex_data']['position'] = []
        model['vertex_data']['uv'] = []
        model['vertex_data']['normals'] = []
        model['vertex_data']['vertex_color'] = []
        model['index_buffer'] = []

        # TODO it's the job of the artist to triangulate the models, sorry, but raise exceptions anyway
        # TODO currently not using the vertex paint data
        uv_layer = bmesh_mesh.loops.layers.uv.active
        print("Object has " + str(len(bmesh_mesh.faces)) + " faces")
          # Go through each face (and subsequently each vertex)
        for i, face in enumerate(bmesh_mesh.faces):
            triangle_indices = []
            triangle = face.loops  # See comment above

            for l_i in range(3):
                triangle_vertex = triangle[l_i]
                x = triangle[l_i].vert.co[0]
                y = triangle[l_i].vert.co[1]
                z = triangle[l_i].vert.co[2]
                uv_data = triangle_vertex[uv_layer].uv
                u = uv_data[0]
                v = uv_data[1]
                nx = triangle[l_i].vert.normal[0]
                ny = triangle[l_i].vert.normal[1]
                nz = triangle[l_i].vert.normal[2]
                # -1 ==> no vertex found
                found_vertex = -1
                # Compare if its exact data is already used in vertex_data
                for vertex_index in range(len(model['vertex_data']['position'])):
                    pos = model['vertex_data']['position'][vertex_index]
                    uvs = model['vertex_data']['uv'][vertex_index]
                    normals = model['vertex_data']['normals'][vertex_index]
                    # TODO add vertex paint stuff
                    # Two ifs for readability # TODO nah
                    if pos[0] != x or pos[1] != y or pos[2] != z or uvs[0] != u or uvs[1] != v:
                        continue
                    if normals[0] != nx or normals[1] != ny or normals[2] != nz:
                        continue
                    # If we got this far we found a matching vertex
                    found_vertex = vertex_index
                    break  # End for loop

                if found_vertex == -1:  # We have to create a new vertex in the list
                    model['vertex_data']['position'].append([x, y, z])
                    model['vertex_data']['uv'].append([u, v])
                    model['vertex_data']['normals'].append([nx, ny, nz])
                    # TODO read the correct vertex_color data
                    model['vertex_data']['vertex_color'].append([255, 255, 255, 255])
                    found_vertex = len(model['vertex_data']['position']) - 1

                # Add index to triangle_indices (the current triangle
                triangle_indices.append(found_vertex)

                # Either way ==>
            # Add triangle to index_buffer
            model['index_buffer'].append(triangle_indices)

        # Write bounding box data
        # Get x/y/z and min/max of the vertices
        x_min = y_min = z_min = 0
        x_max = y_max = z_max = 0
        for position in model['vertex_data']['position']:
            x_min = min(x_min, position[0])
            x_max = max(x_max, position[0])
            y_min = min(y_min, position[1])
            y_max = max(y_max, position[1])
            z_min = min(z_min, position[2])
            z_max = max(z_max, position[2])

        model['bounding_box'] = [x_min, y_min, z_min, x_max, y_max, z_max]

    # Setup path
    working_directory = os.path.dirname(filename)

    # Write sgi
    # TODO currently no animations, add them to sgi and sgm
    with open(filename, 'wb') as f:
        write_pascal_string(f, "2.0")
        f.write(struct.pack('>Q', len(models)))
        for k, current_model in models.items():
            write_pascal_string(f, current_model['element_name'])
            write_pascal_string(f, current_model['shape_name'])
            # world matrix
            for entry in current_model['world_matrix']:
                f.write(struct.pack('>f', entry))
            # Write if object is visible
            f.write(struct.pack('B', current_model['is_visible']))
            # unknown TODO unknown, fix it, default value 0
            f.write(struct.pack('B', 0))
            # Number of animations, 0 for now (see todo above with statement)
            n_of_animations = 0
            f.write(struct.pack('>Q', n_of_animations))
    # Write sgm
    # Currently no data for sgs in the vertex data (the joints?)
    for key, model in models.items():
        with open(os.path.join(working_directory, model['shape_name'] + '.sgm.msb'), 'wb') as f:
            # Write header data
            write_pascal_string(f, "2.0")
            write_pascal_string(f, model['texture_name'])
            # 13 unknown floats, seems to be the same in every file
            f.write(struct.pack('>fffffffffffff', 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 90))
            # Write rest of header
            write_pascal_string(f, "float p[3],n[3],uv[2]; uchar4 c;")  # TODO Doesn't work for files with sgs
            f.write(struct.pack('>Q', 36))  # TODO doesn't work for files with sgs
            f.write(struct.pack('>Q', len(model['vertex_data']['pos'])))  # Write number of vertices
            f.write(struct.pack('>Q', len(model['index_buffer'])))  # Write number of triangles (loops)
            f.write(struct.pack('>Q', 0))  # TODO doesn't work for sgs files
            for vertex_index in range(len(model['vertex_data']['position'])):
                # Write vertex data
                f.write(struct.pack('>f', model['vertex_data']['position'][vertex_index][0]))
                f.write(struct.pack('>f', model['vertex_data']['position'][vertex_index][1]))
                f.write(struct.pack('>f', model['vertex_data']['position'][vertex_index][2]))

                f.write(struct.pack('>f', model['vertex_data']['normals'][vertex_index][0]))
                f.write(struct.pack('>f', model['vertex_data']['normals'][vertex_index][1]))
                f.write(struct.pack('>f', model['vertex_data']['normals'][vertex_index][2]))

                f.write(struct.pack('>f', model['vertex_data']['uv'][vertex_index][0]))
                f.write(struct.pack('>f', model['vertex_data']['uv'][vertex_index][1]))

                f.write(struct.pack('>B', model['vertex_data']['vertex_color'][vertex_index][0]))
                f.write(struct.pack('>B', model['vertex_data']['vertex_color'][vertex_index][1]))
                f.write(struct.pack('>B', model['vertex_data']['vertex_color'][vertex_index][2]))
                f.write(struct.pack('>B', model['vertex_data']['vertex_color'][vertex_index][3]))
            for triangle in model['index_buffer']:
                # Write index buffer
                for i in range(3):
                    f.write(struct.pack('>H', triangle[i]))
            # Write the bounding box data TODO check if anything changes with files with a sgs
            for i in range(6):
                f.write(struct.pack('>f', model['vertex_data']['bounding_box'][i]))
            # TODO no sgs is currently written
            # Write bone names
            # Write bone data

    # Write sga
    # Write sgs

    return {'FINISHED'}


def write_pascal_string(f, string):
    ascii_string = string.encode('ascii')
    f.write(struct.pack('>Q', len(ascii_string)))
    f.write(ascii_string)


def get_mat4(mat):
        return [mat[0][0], mat[1][0], mat[2][0], mat[3][0],
                mat[0][1], mat[1][1], mat[2][1], mat[3][1],
                mat[0][2], mat[1][2], mat[2][2], mat[3][2],
                mat[0][3], mat[1][3], mat[2][3], mat[3][3]]