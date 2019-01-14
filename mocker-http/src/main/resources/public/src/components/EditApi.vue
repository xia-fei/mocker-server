<template>
  <div>
    <label for="code"></label>
    <textarea id="code"></textarea>
    <div style="position: fixed;
    top: 300px;
    right: 130px;">
      <Button type="primary" @click="saveData()">
        保存数据
        <Icon type="ios-arrow-forward"></Icon>
      </Button>
    </div>
  </div>
</template>
<script>
  import CodeMirror from 'codemirror'
  import 'codemirror/theme/idea.css'
  import 'codemirror/lib/codemirror.css'
  import 'codemirror/theme/idea.css'
  import 'codemirror/addon/fold/foldgutter.css'
  import 'codemirror/mode/javascript/javascript'
  import 'codemirror/addon/fold/foldgutter'
  import 'codemirror/addon/fold/foldcode'
  import 'codemirror/addon/fold/indent-fold'
  import 'codemirror/addon/fold/brace-fold'
  import 'codemirror/addon/fold/xml-fold'
  import 'codemirror/mode/htmlmixed/htmlmixed.js'

  export default {
    data() {
      return {
        editor: undefined,
        apiContent: undefined
      }
    },
    created: function () {

    },
    methods: {
      saveData: function () {
        let value = this.editor.getValue();
        this.$axios.post(this.$API_URL + '/saveData',JSON.parse(value)
        ).then((res) => {
          window.location.href=this.$API_URL + '/data/' + res.data.id;
        })
      },
      getParam: function (key) {
        let searchStr = window.location.search.substr(1);
        let searchArray = searchStr.split("&");
        for (let i = 0; i < searchArray.length; i++) {
          let param = searchArray[i];
          let paramArray = param.split("=");
          if (paramArray[0] === key) {
            return paramArray[1];
          }
        }

      }
    },
    mounted: function () {
      this.editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        mode: 'application/ld+json',
        lineNumbers: true,
        lineWrapping: true,
        'theme': 'idea',
        foldGutter: true,
        gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
      });
      let reqParameter = decodeURIComponent(this.getParam('apiParameter'));
      console.log(this.$API_URL + reqParameter);
      this.$axios.get(this.$API_URL + reqParameter).then((res) => {
        this.editor.setValue(JSON.stringify(res.data, null, 2));
        this.editor.foldCode(CodeMirror.Pos(13, 0));
      }).catch((error) => {
      })
    }

  }
</script>
<style>
  .CodeMirror {
    border: 1px solid #eee;
    height: auto;
    width: 800px;
    margin: 0 auto;
  }
</style>
