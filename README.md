# SanDict
This is an open source Android dictionary application which support `stardict` format dictionaries.

Current status: Can build and run. But app is slow, misses many frames and gets stuck, and does not find dictionaries. 

## Fork information
This fork of a fork of the abandoned [namndev/Qdict](https://github.com/namndev/QDict), created to incorporate patches and serve the special purposes of the sanskrit community.

## Using dictionaries
QDict support 3 ways to search:  `Glob-style pattern matching`,  `Fuzzy query` and `Full-text` search.

Stardict dictionaries are not included in the application and should be obtained separately.

Dictionaries should be placed in `/sdcard/dictdata`.
Each dictionary should be placed into a separate subfolder, for instance `/sdcard/dictdata/dict-name`.
It's recommended to only use alphanumeric filenames.