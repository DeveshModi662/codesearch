# BabyGrok
A basic CLI tool codesearch based on exact string matching of pattern matching.

## Installation 
1. Download the ZIP
2. Unzip the file
3. Add the extracted folder to system path
4. Restart the terminal if already open

## Commands

### Build the index

```
babygrok create <codeBasePath> <indexStoragePath>
eg. babygrok create C:\Users\Documents\Deleteme\MyRepo C:\Users\Documents\Deleteme\MyIndex
```

### Exact match search
```
babygrok search --content=<searchString> 
eg. babygrok search --content=zulu
```

### Pattern match search
```
babygrok search --content=<*-separatedSearchString>
eg. babygrok search --content=*zulu*alias*
```
