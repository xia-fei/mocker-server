import Vue from 'vue'
import Router from 'vue-router'
import Index from '@/components/Index'
import EditApi from '@/components/EditApi'
Vue.use(Router);

export default new Router({
  mode:'history',
  routes: [
    {
      path: '/',
      component: Index
    },
    {
      path: '/edit-api.html',
      component: EditApi
    }
  ]
})
