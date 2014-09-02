import os
import struct
import shutil
import app_info
from SkullModPy.common.util import tag


def obj_writer(file_path, xyz_list, indices_list):
    with open(file_path, 'w', encoding='ascii') as file:
        for xyz in xyz_list:
            file.write('v ' + str(xyz[0]) + ' ' + str(xyz[1]) + ' ' + str(xyz[2]) + "\n")
        for indices in indices_list:
            # OBJ index for vertices starts at 1 *grumble*
            file.write('f ' + str(indices[0] + 1) + ' ' + str(indices[1] + 1) + ' ' + str(indices[2] + 1) + "\n")


def collada_export(file_path, texture_directory, scene_name, sgm_data, sgi_data):
        """
        Only tested with Blender 2.71 import
        Any version below 2.71 does not import vertex colors

        Make a simple collada file, NOT containing the following:
        BONES: more work, later
        BONE ANIMATION: can blender import this correctly from a dae file?
        UV animation: how to define this in collada, check spec

        You're reading this? Nice. Feel special

        Why use collada? Only format that is free and can handle scenes with nearly any complexity
        Why not use pycollada? NumPy, ain't redistributing that if I can avoid it
        Btw model ordering is preserved, no idea if it is required, but it is done anyway
        Blender is only using polylist and no triangles because cupcakes,
        but it triangulates correctly when exporting so no problems here
        (The actual reason is because polylist is as valid as any other way to save data in collada)
        Why Y_UP? Because Mr DirectX says so

        :param file_path: Path to output file
        :param scene_name: name of the scene
        :param models: An array of all models in as dictionaries
        """
        # Tell the command line we like it
        print('Thank you for flying with COLLADAeral Airlines')

        # We need the following data in advance to write the collada file:

        # All texture names
        texture_names = {}
        shape_names = []
        # TODO find a better way to remove duplicates, this is ugly
        for sgm in sgm_data:
            texture_names[str(sgm['texture_name'])] = True
        # Redefine texture names to be a list of keys
        texture_names = texture_names.keys()

        # Define base collada stuff
        xml_start = '<?xml version="1.0" encoding="utf-8"?>'
        collada_start = '<COLLADA xmlns="http://www.collada.org/2005/11/COLLADASchema" version="1.4.1">'
        # Write the dae

        with open(file_path, 'w', encoding='utf-8') as file:
            file.write(xml_start)
            file.write(collada_start)

            # asset start (required)
            file.write(tag('asset', False))
            # contributor start
            file.write(tag('contributor', False))
            file.write(tag('authoring_tool', False) + 'SkullModPy v'
                       + app_info.APPLICATION_VERSION + ' ' + app_info.APPLICATION_DATE + tag('authoring_tool'))
            file.write(tag('contributor'))
            # contributor end
            file.write(tag('up_axis', False) + 'Y_UP' + tag('up_axis'))
            file.write(tag('asset'))
            # asset end

            # library_images start
            file.write(tag('library_images', False))
            for texture_name in texture_names:
                file.write('<image id="' + texture_name + '_dds" name="' + texture_name + '_dds">')
                file.write(tag('init_from', False) + texture_name + '.dds' + tag('init_from'))
                file.write(tag('image'))
            file.write(tag('library_images'))
            # library_images end

            # library_effects start
            file.write(tag('library_effects', False))
            for texture_name in texture_names:
                file.write('<effect id="' + texture_name + '-effect">')
                file.write(tag('profile_COMMON', False))

                file.write('<newparam sid="' + texture_name + '_dds-surface">')
                file.write('<surface type="2D">')
                file.write(tag('init_from', False) + texture_name + '_dds' + tag('init_from'))
                file.write(tag('surface'))
                file.write(tag('newparam'))

                file.write('<newparam sid="' + texture_name + '_dds-sampler">')
                file.write(tag('sampler2D', False))
                file.write(tag('source', False) + texture_name + '_dds-surface' + tag('source'))
                file.write(tag('sampler2D'))
                file.write(tag('newparam'))

                file.write('<technique sid="common">')
                file.write(tag('lambert', False))
                file.write(tag('emission', False))
                file.write('<color sid="emission">0 0 0 1</color>')
                file.write(tag('emission'))
                file.write(tag('ambient', False))
                file.write('<color sid="ambient">0 0 0 1</color>')
                file.write(tag('ambient'))
                file.write(tag('diffuse', False))
                file.write('<texture texture="' + texture_name + '_dds-sampler" texcoord=""/>')
                file.write(tag('diffuse'))
                file.write(tag('index_of_refraction', False))
                file.write('<float sid="index_of_refraction">1</float>')
                file.write(tag('index_of_refraction'))
                file.write(tag('lambert'))
                file.write(tag('technique'))

                file.write(tag('profile_COMMON'))
                file.write(tag('effect'))
            file.write(tag('library_effects'))
            # library_effects start

            # library_materials start
            file.write(tag('library_materials', False))
            for texture_name in texture_names:
                file.write('<material id="' + texture_name + '-material" name="' + texture_name + '">')
                file.write('<instance_effect url="#' + texture_name + '-effect"/>')
                file.write(tag('material'))
            file.write(tag('library_materials'))
            # library_materials end

            # library_geometries start
            file.write(tag('library_geometries', False))
            for i in range(0, sgi_data.__len__()):
                file.write('<geometry id="' + (sgi_data[i])['shape_name'] + '-mesh" name="' + (sgi_data[i])['shape_name'] + '">')
                file.write(tag('mesh', False))

                file.write('<source id="' + (sgi_data[i])['shape_name'] + '-mesh-positions">')
                #POSITION DATA
                file.write('<float_array id="' + (sgi_data[i])['shape_name'] + '-mesh-positions-array" count="' + str(3*(sgm_data[i])['vertices'].__len__()) + '">')
                vertex_data = ""
                for vertex in (sgm_data[i])['vertices']:
                    vertex_data += "{:6g}".format(struct.unpack('>f', vertex[0:4])[0]).strip() + ' ' + "{:6g}".format(struct.unpack('>f', vertex[4:8])[0]).strip() + ' ' + "{:6g}".format(struct.unpack('>f', vertex[8:12])[0]).strip() + ' '
                file.write(vertex_data.strip())
                file.write(tag('float_array'))
                file.write(tag('technique_common', False))
                file.write('<accessor source="#' + (sgi_data[i])['shape_name'] + '-mesh-positions-array" count="' + str((sgm_data[i])['vertices'].__len__()) + '" stride="3">')
                file.write('<param name="X" type="float"/>')
                file.write('<param name="Y" type="float"/>')
                file.write('<param name="Z" type="float"/>')
                file.write(tag('accessor'))
                file.write(tag('technique_common'))
                file.write(tag('source'))

                file.write('<source id="' + (sgi_data[i])['shape_name'] + '-mesh-normals">')
                file.write('<float_array id="' + (sgi_data[i])['shape_name'] + '-mesh-normals-array" count="' + str(3*(sgm_data[i])['vertices'].__len__()) + '">')
                # NORMALS DATA
                normal_data = ""
                for vertex in (sgm_data[i])['vertices']:
                    normal_data += "{:6g}".format(struct.unpack('>f', vertex[12:16])[0]).strip() + ' ' + "{:6g}".format(struct.unpack('>f', vertex[16:20])[0]).strip() + ' ' + "{:6g}".format(struct.unpack('>f', vertex[20:24])[0]).strip() + ' '
                file.write(normal_data.strip())
                file.write(tag('float_array'))
                file.write(tag('technique_common', False))
                file.write('<accessor source="#' + (sgi_data[i])['shape_name'] + '-mesh-normals-array" count="' + str((sgm_data[i])['vertices'].__len__()) + '" stride="3">')
                file.write('<param name="X" type="float"/>')
                file.write('<param name="Y" type="float"/>')
                file.write('<param name="Z" type="float"/>')
                file.write(tag('accessor'))
                file.write(tag('technique_common'))
                file.write(tag('source'))

                file.write('<source id="' + (sgi_data[i])['shape_name'] + '-mesh-map">')
                file.write('<float_array id="' + (sgi_data[i])['shape_name'] + '-mesh-map-array" count="' + str(2*(sgm_data[i])['vertices'].__len__()) + '">')
                # UV DATA
                uv_data = ""
                for vertex in (sgm_data[i])['vertices']:
                    uv_data += "{:6g}".format(struct.unpack('>f', vertex[24:28])[0]).strip() + ' ' + "{:6g}".format(struct.unpack('>f', vertex[28:32])[0]).strip() + ' '
                file.write(uv_data.strip())
                file.write(tag('float_array'))
                file.write(tag('technique_common', False))
                file.write('<accessor source="#' + (sgi_data[i])['shape_name'] + '-mesh-map-array" count="' + str((sgm_data[i])['vertices'].__len__()) + '" stride="2">')
                file.write('<param name="S" type="float"/>')
                file.write('<param name="T" type="float"/>')
                file.write(tag('accessor'))
                file.write(tag('technique_common'))
                file.write(tag('source'))

                file.write('<source id="' + (sgi_data[i])['shape_name'] + '-mesh-colors">')
                file.write('<float_array id="' + (sgi_data[i])['shape_name'] + '-mesh-colors-array" count="' + str(3*(sgm_data[i])['vertices'].__len__()) + '">')
                # VERTEX COLOR DATA
                v_color_data = ""
                for vertex in (sgm_data[i])['vertices']:
                    v_color_data += '1 1 1 '
                file.write(v_color_data.strip())
                file.write(tag('float_array'))
                file.write(tag('technique_common', False))
                file.write('<accessor source="#' + (sgi_data[i])['shape_name'] + '-mesh-colors-array" count="' + str((sgm_data[i])['vertices'].__len__()) + '" stride="3">')
                file.write('<param name="R" type="float"/>')
                file.write('<param name="G" type="float"/>')
                file.write('<param name="B" type="float"/>')
                file.write(tag('accessor'))
                file.write(tag('technique_common'))
                file.write(tag('source'))

                file.write('<vertices id="' + (sgi_data[i])['shape_name'] + '-mesh-vertices">')
                file.write('<input semantic="POSITION" source="#' + (sgi_data[i])['shape_name'] + '-mesh-positions"/>')
                file.write(tag('vertices'))

                file.write('<polylist material="' + (sgm_data[i])['texture_name'] + '-material" count="' + str((sgm_data[i])['index_buffer'].__len__()) + '">')
                file.write('<input semantic="VERTEX" source="#' + (sgi_data[i])['shape_name'] + '-mesh-vertices" offset="0"/>')
                file.write('<input semantic="NORMAL" source="#' + (sgi_data[i])['shape_name'] + '-mesh-normals" offset="1"/>')
                file.write('<input semantic="TEXCOORD" source="#' + (sgi_data[i])['shape_name'] + '-mesh-map" offset="2" set="0"/>')
                file.write('<input semantic="COLOR" source="#' + (sgi_data[i])['shape_name'] + '-mesh-colors" offset="3"/>')
                file.write(tag('vcount', False) + "3 "*(sgm_data[i])['index_buffer'].__len__() + tag('vcount'))
                file.write(tag('p', False))
                triangle_offsets = ""
                for triangle in (sgm_data[i])['index_buffer']:
                    # See documentation of the <p> element of a polyline for this list
                    triangle_offsets += (str(triangle[0]) + ' ')*4 + (str(triangle[1]) + ' ')*4 + (str(triangle[2]) + ' ')*4
                file.write(triangle_offsets.strip())
                file.write(tag('p'))

                file.write(tag('polylist'))

                file.write(tag('mesh'))
                file.write(tag('geometry'))
            file.write(tag('library_geometries'))
            # library_geometries end

            # library_visual_scenes start
            file.write(tag('library_visual_scenes', False))
            file.write('<visual_scene id="' + scene_name + '" name="' + scene_name + '">')

            for i in range(0, sgi_data.__len__()):
                file.write('<node id="' + (sgi_data[i])['shape_name'] + '" name="' + (sgi_data[i])['shape_name'] + '" type="NODE">')
                matrix_string = ""
                for float_value in (sgi_data[i])['mat4']:
                    matrix_string += "{:6g}".format(float_value).strip() + " "
                file.write('<matrix sid="transform">' + matrix_string.strip() + tag('matrix'))
                file.write('<instance_geometry url="#' + (sgi_data[i])['shape_name'] + '-mesh">')
                file.write(tag('bind_material', False))
                file.write(tag('technique_common', False))
                file.write('<instance_material symbol="' + (sgm_data[i])['texture_name'] + '-material" target="#' + (sgm_data[i])['texture_name'] + '-material">')
                file.write('<bind_vert_input semantic="' + (sgm_data[i])['texture_name'] + '" input_semantic="TEXCOORD" input_set="0"/>')
                file.write(tag('instance_material'))
                file.write(tag('technique_common'))
                file.write(tag('bind_material'))
                file.write(tag('instance_geometry'))
                file.write(tag('node'))
            file.write(tag('visual_scene'))
            file.write(tag('library_visual_scenes'))
            # library_visual_scenes end

            # scene start
            file.write(tag('scene', False))
            file.write('<instance_visual_scene url="#' + scene_name + '"/>')
            file.write(tag('scene'))
            # scene end

            file.write(tag('COLLADA'))
        # Now copy all the textures in the resulting folder
        for texture_name in texture_names:
            shutil.copy2(os.path.join(texture_directory, texture_name + '.dds'), os.path.join(os.path.dirname(file_path), texture_name + '.dds'))
