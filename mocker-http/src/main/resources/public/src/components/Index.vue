<template>
  <div>
    <br>
    <Row>
      <Col span="2" offset="22"><a style="font-size: 14px" href="quick-start.html">用户指南</a></Col>
    </Row>
    <br>
    <br>
    <Row>
      <Col span="8" offset="8">
        <label for="code"></label>
        <textarea id="code"></textarea>
      </Col>
    </Row>
    <br>
    <br>
    <br>
    <Col span="14" offset="5">
      <Row class="code-row-bg">
        <i-col span="18" style="padding-right:10px">
          <i-select v-model="className" filterable>
            <i-option v-for="item in classList" :value="item" :key="item">{{item}}</i-option>
          </i-select>
        </i-col>
        <i-col span="6">
          <i-button type="success" :loading="loading" @click="loadClass">加载Class对象</i-button>
        </i-col>
      </Row>
      <br>
      <br>
      <Row>
        <i-col span="6">
          <radio-group v-model="struct" type="button">
            <Radio label="bean">普通对象</Radio>
            <Radio label="list">数组</Radio>
            <Radio label="page">分页</Radio>
          </radio-group>
        </i-col>
        <i-col span="9">
          <span>数组大小</span>
          <input-number :max="100" :min="1" v-model="listSize"></input-number>
          <span>递归次数</span>
          <input-number :max="10" :min="1" v-model="depth"></input-number>
        </i-col>
        <i-col span="3" offset="1">
          <i-button type="primary" @click="customize">自定义数据</i-button>
        </i-col>
        <i-col span="3">
          <i-button type="primary" @click="mockJump">Mock数据</i-button>
        </i-col>

      </Row>

    </Col>
    <br>
    <br>
    <br>


  </div>

</template>
<script>
  import axios from 'axios'
  import CodeMirror from 'codemirror'
  import 'codemirror/theme/idea.css'
  import 'codemirror/lib/codemirror.css'
  import 'codemirror/addon/hint/show-hint'
  import 'codemirror/addon/hint/xml-hint'
  import 'codemirror/mode/xml/xml'



  export default {
    data() {
      return {
        loading: false,
        classList: {},
        className: "",
        listSize: 10,
        depth: 3,
        struct: "bean",
        editor: undefined
      }
    },
    methods: {

      loadClass: function () {
        this.savePomStorage(this.editor.getValue());
        let pomLocation = this.getPomLocation();
        this.loading = true;
        axios.get(this.$API_URL + '/getJarClass', {
          params: {
            g: pomLocation.groupId, a: pomLocation.artifactId, v: pomLocation.version
          }
        }).then((res) => {
          console.log(res.data.jarUrl);
          this.classList = res.data.classList;
          this.loading = false;
          this.$Message.success('加载' + res.data.classList.length + '个对象');
        }).catch((error) => {
          console.log(error);
          this.loading = false;
        })
      },
      getPomLocation: function () {
        let pomXml = this.editor.getValue();
        let groupId = this.getElementText(pomXml, 'groupId');
        let artifactId = this.getElementText(pomXml, 'artifactId');
        let version = this.getElementText(pomXml, 'version');
        return {groupId: groupId, artifactId: artifactId, version: version}
      },
      getElementText: function (text, tag) {
        let reg = new RegExp('<' + tag + '>(.*)</' + tag + '>');
        return reg.exec(text)[1];
      },
      getApiParam: function () {
        if (this.className === undefined || this.className === '') {
          this.$Message.error('请选择一个Class对象');
          return;
        }
        let url = '/';
        let pomLocation = this.getPomLocation();
        url += pomLocation.groupId + "/" + pomLocation.artifactId + "/" + pomLocation.version + "/" + this.className;
        let queryString = '';
        if (this.struct !== 'bean') {
          queryString = this.appParam(queryString, 'struct', this.struct);
        }
        if (this.listSize !== 10) {
          queryString = this.appParam(queryString, 'listSize', this.listSize);
        }
        if (this.depth !== 3) {
          queryString = this.appParam(queryString, 'depth', this.depth);
        }

        if (queryString !== '') {
          url += '?' + queryString;
        }
        return url;
      },
      mockJump: function () {
        window.open(this.$API_URL + this.getApiParam());
      },
      customize: function () {
        window.open('edit-api.html?apiParameter=' + encodeURIComponent(this.getApiParam()));
      },
      appParam: function (queryString, k, v) {
        let and = '';
        if (queryString !== '') {
          and = '&';
        }
        queryString += and + k + '=' + v;
        return queryString;
      },
      getPomStorage: function () {
        return window.localStorage.getItem("pom")
      },
      savePomStorage: function (pomXml) {
        window.localStorage.setItem("pom", pomXml);
      }

    }
    ,
    mounted: function () {
      this.editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        mode: 'xml',
        lineNumbers: true,
        'theme': 'idea'
      });
      console.log(this.editor);

      this.editor.setSize('490px', '120px');
      this.editor.setValue("  <dependency>\n" +
        "         <groupId>com.qccr.goodscenter</groupId>\n" +
        "         <artifactId>goodscenter-facade</artifactId>\n" +
        "         <version>4.1.3.18</version>\n" +
        "</dependency>");

      let pomStorage = this.getPomStorage();
      if (pomStorage !== null) {
        this.editor.setValue(pomStorage);
      }
    }


  }

</script>
<style>
  .CodeMirror {
    border: solid 1px #b8b7c0;
    margin: 0 auto;
    font-size: 14px;
  }
</style>
