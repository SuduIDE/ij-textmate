# Injected language in IDEA CE

# Contents

- [Contents](#contents)
- [Motivation and terms](#motivation-and-terms)
- [Short description](#short-description)
- [Install plugin](#install-plugin)
- [Usage](#usage)
- [Features](#features)
- [Bugs](#bugs)

## Motivation and terms

Sometimes a person who deals with code has to write code fragments in another language inside string literals.

A good example is `SQL` queries that have to be written inside string literals in `Java`, `PHP`, `Python` backend developer:

![example_sql_query](images/example_sql_query.png)

Or `bash` script inside a yaml file:

![bash_yaml](images/bash_yaml.png)

In order to make it easier to work with such code fragments, there are language injections. Language injections allow you to add syntax highlighting and completions to code fragments inside a string literal

## Short description

Plugin for language injection. Provides language injection in string literals. The injected languages can be those for
which [TextMate Bundles](https://www.jetbrains.com/help/webstorm/tutorial-using-textmate-bundles.html) are available.

Supports saving injections between IDE restarts and completions for the injected language.

## Requirements

| IDE                             | min version | max version | status                |
|---------------------------------|-------------|-------------|-----------------------|
| IntelliJ IDEA Community Edition | 2022.2      | 2023.1.5    | supported             |
| IntelliJ IDEA Community Edition | 2023.2      | 2023.2      | currently unsupported |
| IntelliJ IDEA Ultimate          | 2022.2      | 2023.1.5    | supported             |
| CLion                           | 2022.2      | 2023.1      | supported             |
| PyCharm Professional Edition    | 2022.2      | 2023.1.4    | supported             |
| PyCharm Community Edition       | 2022.2      | 2023.1      | supported             |
| PhpStorm                        | 2022.2      | 2023.1      | supported             |
| Rider                           | 2022.2.4    | 2023.1.4    | supported             |
| GoLang                          | 2022.2.6    | 2023.1.4    | supported             |
| RubyMine                        | 2022.2.5    | 2023.1.5    | supported             |

Supported languages: `Java`, `Kotlin`, `python`, `C++`, `PHP`, `TypeScript`, `C#`, `Go`, `Ruby`

## Install plugin

1. Go to the [Releases](https://github.com/SuduIDE/ij-textmate) section
2. Select the assets you are interested in
3. Download `ij-textmate-*.jar` file. `*` - any string
4. Open `intellij IDEA`
5. Press `Ctrl+Alt+S` to open the IDE settings and select Plugins
6. Select the `Plugins` tab
7. Click on the gears to the right of the `Installed` button and then click `Install Plugin from Disk…`
8. Select the plugin archive file and click `OK`
9. Click OK to apply the changes and restart the IDE if prompted

## Usage

1. Select the string literal in which we want to embed the language. To do this, move the caret to a position inside the
   string literal

   ![string literal without injection](images/without_injection.png)

2. First press `Ctrl+\` then press `Ctrl+I`. A list of possible injection languages will appear. Use the keys to
   navigate through the list:
    - `↑` - move up
    - `↓` - move down

   ![select language](images/select_language.png)
3. Press `enter` to confirm your choice

   ![with injection](images/with_injection.png)

4. To remove an injection, first press `Ctrl+\` then press `Ctrl+I`

## Features

- You can also search by name when selecting languages. To do this, just type the name of the language you need

  ![search_language](images/search_language.png)

- Support completion for injected language

  ![variant_completion](images/variant_completion.png)

- Injections are saved when the IDE is restarted

- The list of supported languages depends on the mappings loaded by Textmate Bundles

  ![textmate_bundles](images/textmate_bundles.png)

## Bugs

- Injected language locale in string literals with references only works for `java`.

- Error occurs during initialization `PersistentStateComponent` in all ide except Intellij.
