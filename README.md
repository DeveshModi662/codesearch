A basic CLI tool codesearch based on exact string matching of pattern matching.

Installation : 
Download the ZIP
Unzip the file
Add the extracted folder to system path
Restart the terminal if already open

Commands :
Build the index
babygrok create <codeBasePath> <indexStoragePath>
eg. babygrok create C:\Users\Documents\Deleteme\MyRepo C:\Users\Documents\Deleteme\MyIndex

Exact match search
babygrok search --content=<searchString> 
eg. babygrok search --content=zulu

Pattern match search
babygrok search --content=<*-separatedSearchString>
eg. babygrok search --content=*zulu*alias*
