<template>
  <div>
    <label for="code"></label>
    <textarea id="code"></textarea>
  </div>
</template>
<script>
  import CodeMirror from 'codemirror'
  import 'codemirror/theme/idea.css'
  import 'codemirror/lib/codemirror.css'
  import 'codemirror/addon/hint/show-hint'
  import 'codemirror/addon/hint/xml-hint'
  import 'codemirror/mode/xml/xml'




  export default {
    mounted: function () {
      var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        mode:'xml',
        lineNumbers: true,
        'theme': 'idea'
      });
      editor.setValue('<?xml version="1.0" encoding="UTF-8"?>\n' +
        '<project xmlns="http://maven.apache.org/POM/4.0.0"\n' +
        '         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\n' +
        '         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">\n' +
        '    <modelVersion>4.0.0</modelVersion>\n' +
        '\n' +
        '    <groupId>org.wing.mocker</groupId>\n' +
        '    <artifactId>parent</artifactId>\n' +
        '    <version>1.0</version>\n' +
        '    <modules>\n' +
        '        <module>mocker-core</module>\n' +
        '        <module>mocker-annotation</module>\n' +
        '        <module>mocker-http</module>\n' +
        '    </modules>\n' +
        '\n' +
        '    <packaging>pom</packaging>\n' +
        '\n' +
        '    <build>\n' +
        '        <plugins>\n' +
        '            <plugin>\n' +
        '                <groupId>org.apache.maven.plugins</groupId>\n' +
        '                <artifactId>maven-compiler-plugin</artifactId>\n' +
        '                <version>3.1</version>\n' +
        '                <configuration>\n' +
        '                    <source>1.7</source>\n' +
        '                    <target>1.7</target>\n' +
        '                </configuration>\n' +
        '            </plugin>\n' +
        '        </plugins>\n' +
        '    </build>\n' +
        '\n' +
        '\n' +
        '</project>')
    }


  }
</script>
