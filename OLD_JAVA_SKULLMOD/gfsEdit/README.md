gfsEdit
========

Packer/Unpacker for .gfs files found in the data01 directory


FAQ

Q: When unpacking and repacking a file from the game the two files have the
same size but aren't binary compatible! Why?

A: I use a depth-first search for the files to include in the .gfs file.
Because of that files are arranged differently within the new .gfs file.


Q: The new .gfs files don't work with the game, fix it!

A: A few files are not allowed to be modified (they are checked by the game).
Rule of thumb: If the file is below 100 MB in size it probably can not be changed.