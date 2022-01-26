Project 1
Jason Dykstra
Student ID: 651341552

Section I: Compiling

In order to run the code, simply open the project directory, navigate to the "src" folder, and then in a command line run "java CS4432_Project1_jpdykstra <numFrames>" and the program will run with the specified number of frames.

If the .class files are not present or need to be re-compiled, you can run "javac CS4432_Project1_jpdykstra.java"

To get a list of possible commands when the program starts, type "HELP". Just remember to use all caps for the commands!

Section II: Test Results

After running the commands in the tests text file, I have passed all tests successfully. There were some minor differences such as printing "File 2 has been unpinned" versus printing "File 2 is now unpinned" But the output gives the same information.

Section III: Design Decisions

Instead of creating an error handler for pulling files from memory, I instead returned -1 in my function that searched for an empty frame, and if -1 was returned each method (GET, SET, PIN, UNPIN) would handle each case individually. 