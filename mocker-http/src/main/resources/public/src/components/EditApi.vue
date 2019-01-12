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
  import 'codemirror/mode/javascript/javascript'



  export default {
    mounted: function () {
      var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        mode:'javascript',
        lineNumbers: true,
        'theme': 'idea'
      });
    },
    data() {
      return {
        cityList: [
          {
            value: 'New York',
            label: 'New York'
          },
          {
            value: 'London',
            label: 'London'
          },
          {
            value: 'Sydney',
            label: 'Sydney'
          },
          {
            value: 'Ottawa',
            label: 'Ottawa'
          },
          {
            value: 'Paris',
            label: 'Paris'
          },
          {
            value: 'Canberra',
            label: 'Canberra'
          }
        ],
        model11: '',
        model12: []
      }
    }
  }
</script>
