# Injected language in IDEA CE

# Contents

- [Contents](#contents)
- [Terms and motivation](#terms-and-motivation)
- [Short description](#short-description)
- [Install plugin](#install-plugin)
- [Usage](#usage)
- [Features](#features)
- [Bugs](#bugs)

## Terms and motivation

Sometimes a person who deals with code has to write code fragments in another language inside string literals.

For example `SQL` queries inside `Java` string literals:

<a href="https://github.com/SuduIDE/ij-textmate#gh-dark-mode-only">
  <img src="images/dark/spring_petclinic.png" alt="sql_query" />
</a>

<a href="https://github.com/SuduIDE/ij-textmate#gh-light-mode-only">
  <img src="images/light/spring_petclinic.png" alt="sql_query" />
</a>

This is where `language injections` help. They allow you to add `syntax highlighting` and `code completion` inside
string literals.

The language injection plugin is called `IntelliLang`. However, in the `Intellij Idea Community Edition`, many languages
are missing, such as `SQL`. But there are `TextMate Bundles`. With which you can get `syntax highlighting`. This can be
done as follows:
<table>
  <tr>
    <td>
        <a href="https://github.com/SuduIDE/ij-textmate#gh-dark-mode-only"><img src="images/dark/string_literal.png" alt="string_literal" /></a>
        <a href="https://github.com/SuduIDE/ij-textmate#gh-light-mode-only"><img src="images/light/string_literal.png" alt="string_literal" /></a>
    </td>
    <td> There is a string literal </td>
  </tr>
  <tr>
    <td>
        <a href="https://github.com/SuduIDE/ij-textmate#gh-dark-mode-only"><img src="images/dark/string_literal_comment_textmate.png" alt="string_literal_comment_textmate" /></a>
        <a href="https://github.com/SuduIDE/ij-textmate#gh-light-mode-only"><img src="images/light/string_literal_comment_textmate.png" alt="string_literal_comment_textmate" /></a>
    </td>
    <td> Add TextMate injection with IntelliLang. A comment will appear </td>
  </tr>
  <tr>
    <td>
        <a href="https://github.com/SuduIDE/ij-textmate#gh-dark-mode-only"><img src="images/dark/string_literal_comment_sql.png" alt="string_literal_comment_sql" /></a>
        <a href="https://github.com/SuduIDE/ij-textmate#gh-light-mode-only"><img src="images/light/string_literal_comment_sql.png" alt="string_literal_comment_sql" /></a>
    </td>
    <td> Replace "textmate" with "sql" </td>
  </tr>
</table>

---
**NOTE**

The described method does not work in IDE version 2023.2 for all TextMate Bundles

---

There are some problems with the approach described above:

- a comment is required, and it can be deleted by another developer in the team
- it doesn't work if you can't insert a comment
- there is no code completion.

In this solution, the plan is to:

- use `TextMate Bundles` for language highlighting
- provide injection recovery on IDE startup
- perform `syntax highlighting` without using comments. To be able to inject a language where you can't use comments
- add `code completion`
- perform injections regardless of language

## Short description

Plugin for language injection. Provides language injection in string literals. The injected languages can be those for
which [TextMate Bundles](https://www.jetbrains.com/help/webstorm/tutorial-using-textmate-bundles.html) are available.

## Requirements

| IDE                             | min version | max version | status                                                                                                                                                                                                                                                                         |
|---------------------------------|-------------|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| IntelliJ IDEA Community Edition | 2022.2      | 2023.1.5    | supported                                                                                                                                                                                                                                                                      |
| IntelliJ IDEA Community Edition | 2023.2      | 2023.2      | latest release: [ubuntu](https://github.com/SuduIDE/ij-textmate/releases/tag/ij-textmate-plugin-ubuntu-latest-java-19-v0.1.5-intellij_2023.2), [windows](https://github.com/SuduIDE/ij-textmate/releases/tag/ij-textmate-plugin-windows-latest-java-19-v0.1.5-intellij_2023.2) |
| IntelliJ IDEA Ultimate          | 2022.2      | 2023.1.5    | supported                                                                                                                                                                                                                                                                      |
| CLion                           | 2022.2      | 2023.1      | supported                                                                                                                                                                                                                                                                      |
| PyCharm Professional Edition    | 2022.2      | 2023.1.4    | supported                                                                                                                                                                                                                                                                      |
| PyCharm Community Edition       | 2022.2      | 2023.1      | supported                                                                                                                                                                                                                                                                      |
| PhpStorm                        | 2022.2      | 2023.1      | supported                                                                                                                                                                                                                                                                      |
| Rider                           | 2022.2.4    | 2023.1.4    | supported                                                                                                                                                                                                                                                                      |
| GoLang                          | 2022.2.6    | 2023.1.4    | supported                                                                                                                                                                                                                                                                      |
| RubyMine                        | 2022.2.5    | 2023.1.5    | supported                                                                                                                                                                                                                                                                      |

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

    <a href="https://github.com/SuduIDE/ij-textmate#gh-dark-mode-only">
      <img src="images/dark/without_injection.png" alt="without_injection" />
    </a>

    <a href="https://github.com/SuduIDE/ij-textmate#gh-light-mode-only">
      <img src="images/light/without_injection.png" alt="without_injection" />
    </a>

2. First press `Ctrl+\` then press `Ctrl+I`. A list of possible injection languages will appear. Use the keys to
   navigate through the list:
    - `↑` - move up
    - `↓` - move down

    <a href="https://github.com/SuduIDE/ij-textmate#gh-dark-mode-only">
      <img src="images/dark/select_language.png" alt="select_language" />
    </a>

    <a href="https://github.com/SuduIDE/ij-textmate#gh-light-mode-only">
      <img src="images/light/select_language.png" alt="select_language" />
    </a>

3. Press `enter` to confirm your choice

   <a href="https://github.com/SuduIDE/ij-textmate#gh-dark-mode-only">
     <img src="images/dark/with_injection.png" alt="with_injection" />
   </a>

   <a href="https://github.com/SuduIDE/ij-textmate#gh-light-mode-only">
     <img src="images/light/with_injection.png" alt="with_injection" />
   </a>   

4. To remove an injection, first press `Ctrl+\` then press `Ctrl+I`

## Features

- You can also search by name when selecting languages. To do this, just type the name of the language you need

  <a href="https://github.com/SuduIDE/ij-textmate#gh-dark-mode-only">
    <img src="images/dark/search_language.png" alt="search_language" />
  </a>

  <a href="https://github.com/SuduIDE/ij-textmate#gh-light-mode-only">
    <img src="images/light/search_language.png" alt="search_language" />
  </a>

- Support completion for injected language

  <a href="https://github.com/SuduIDE/ij-textmate#gh-dark-mode-only">
    <img src="images/dark/variant_completion.png" alt="variant_completion" />
  </a>

  <a href="https://github.com/SuduIDE/ij-textmate#gh-light-mode-only">
    <img src="images/light/variant_completion.png" alt="variant_completion" />
  </a>

- Injections are saved when the IDE is restarted

  **NOTE:**
  for this feature to work, it is recommended to restart the IDE after installing the plugin


- The list of supported languages depends on the mappings loaded by Textmate Bundles

  <a href="https://github.com/SuduIDE/ij-textmate#gh-dark-mode-only">
    <img src="images/dark/textmate_bundles.png" alt="textmate_bundles" />
  </a>

  <a href="https://github.com/SuduIDE/ij-textmate#gh-light-mode-only">
    <img src="images/light/textmate_bundles.png" alt="textmate_bundles" />
  </a>

- Injection into all places where the variable is used

## Bugs

- Injection into all places of variable usage works only for `Java`
