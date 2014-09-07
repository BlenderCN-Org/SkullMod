import sys
from cx_Freeze import setup, Executable

build_exe_options = {"include_msvcr": True, "icon": "appIcon.ico", "optimize": 2}

base = None
if sys.platform == "win32":
    base = "Console"

setup(
    name='SkullModPy',
    version='0.0.1',
    packages=['SkullModPy'],
    url='http://www.0xfail.net',
    license='MIT',
    classifiers=[
        # How mature is this project? Common values are
        # 3 - Alpha
        # 4 - Beta
        # 5 - Production/Stable
        'Development Status :: 3 - Alpha',
        # Pick your license as you wish (should match "license" above)
        'License :: OSI Approved :: MIT License',
        'Programming Language :: Python :: 3.4'
    ],
    author='0xFAIL',
    author_email='the0xFAIL@gmail.com',
    description='Modding tool for Skullgirls',

    options={"build_exe": build_exe_options},
    executables=[Executable("SkullModPy\SkullMod.py", base=base)]
)
