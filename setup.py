from distutils.core import setup
import py2exe  # Required, see options

# Setup with py2exe
setup(
    console=[{
        "script": "SkullModPy\SkullMod.py",
        "icon_resources": [(1, "appIcon.ico")]
    }],
    options={
        "py2exe": {
            "optimize": 2,
            "compressed": True,
            "bundle_files": 1
        }
    },
    zipfile=None
)
