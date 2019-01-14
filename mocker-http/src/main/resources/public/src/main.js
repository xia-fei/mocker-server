// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import iView from 'iview';
import 'iview/dist/styles/iview.css';
import axios from 'axios'

Vue.config.productionTip = false
Vue.use(iView);

let API_URL = '';
if (process.env.NODE_ENV === 'development') {
  API_URL = 'http://localhost:8090'
}
Vue.prototype.$API_URL = API_URL;
Vue.prototype.$axios=axios;
new Vue({
  el: '#app',
  router,
  components: {App},
  template: '<App/>'
});
