# envfile-plugin
An eclipse plug-in that extends the launch configuration for local Java applications (the default launch configuration type for Java in Eclipse) 
with the possibility to populate environment variables from files. 

The plug-in adds a tab to the launch configuration which allows the user to select one or more text files containing key=value pairs, one per line.
When the launch configuration is executed to start the Java program, the plug-in reads the text files and sets each key=value pair as an environment variable
for the new process.

This is especially useful when an application uses environment variables for differentiating its configuration for different execution environments.

# license
Eclipse Public License 2.0


