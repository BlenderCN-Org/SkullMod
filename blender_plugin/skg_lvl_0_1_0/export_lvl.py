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
            continue  # Skip it if it's not an object
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
        # TODO for now we are using the default uv texture
        uv_layer = bmesh_mesh.loops.layers.uv.active
        print("Object has " + str(len(bmesh_mesh.faces)) + " faces")
          # Go through each face (and subsequently each vertex)
        for i, face in enumerate(bmesh_mesh.faces):
            triangle_indices = []
            triangle = face.loops # See comment above

            for l_i in range(3):
                triangle_vertex = triangle[l_i]
                uv_data = triangle_vertex[uv_layer].uv
                u = uv_data[0]
                v = uv_data[1]
                x = triangle[l_i].vert.co[0]
                y = triangle[l_i].vert.co[1]
                z = triangle[l_i].vert.co[2]
                nx = triangle[l_i].vert.normal[0]
                ny = triangle[l_i].vert.normal[1]
                nz = triangle[l_i].vert.normal[2]
                # Compare if its exact data is already used in vertex_data
                found_vertex = -1
                for k, vertex_data in model['vertex_data'].items():
                    pass # TODO work
                triangle_indices.append(found_vertex)
                # If not ==> add it to vertex_data
                # Either way ==> Add index to triangle_indices
            # Add triangle to index_buffer





    # Write sgi
    # TODO currently no animations, add them

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