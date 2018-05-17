<template>
  <div @mouseenter="mouseEnter()" @mouseleave="mouseLeave()">
    <div class="publisherInfo">
      <router-link :to="{ path: `/user/${questionOverview.publisherId}` }">
        <img class="avatar" :src="questionOverview.publisherImgSrc">
        <span class=".publisher"> {{ questionOverview.publisherName }} </span>
      </router-link>
    </div>
    <div class="summary">
      <h3>
        <router-link :to="{ path : '/questionDetail/' + questionOverview.id }">
          {{ questionOverview.title }}
        </router-link>
        <el-tag v-if="questionOverview.hidden" type="danger" style="font-weight:normal;position: relative;bottom: 2px">被屏蔽</el-tag>
      </h3>
      <div class="types">
        {{ questionOverview.subject }} -> {{ questionOverview.course }}
      </div>
      <div class="tags" v-for="tag in tags">
        <router-link :to="{ path : `/questions/searchQuestion/${tag.text}`}">
          <el-tag style="float:left;margin-right:10px">{{tag}}</el-tag>
        </router-link>
      </div>
      <div v-if='isAdmin' class="publish-date">
        <el-tooltip v-if='this.itemHoverIndex==1&&this.hasHidden' content="取消屏蔽后用户将能浏览此问题" placement="top">
          <el-button style="margin-right: 8px" :disabled="isSendingHidden"  @click.prevent="unHiddenQuestion()"  size="mini" icon="warning" type="warning">取消屏蔽</el-button>
        </el-tooltip>
        <el-tooltip v-else-if='this.itemHoverIndex==1&&!this.hasHidden' content="屏蔽后所有用户将无法浏览此问题" placement="top">
          <el-button style="margin-right: 8px" :disabled="isSendingHidden"  @click.prevent="hiddenQuestion()"  size="mini" icon="circle-cross" type="danger">屏蔽问题</el-button>
        </el-tooltip>
        发布于 <span style="padding-right: 5px">{{ questionOverview.publishDateTime | moment("YYYY-MM-DD ") }} </span>
      </div>
      <div v-else-if='isUser&&!questionOverview.hidden' class="publish-date">
        <el-tooltip v-if="this.itemHoverIndex==1&&this.hasFollow" content="取消关注后将不会获得更新提醒" placement="top">
          <el-button :loading="loadingFollowStatus" style="margin-right: 8px" :disabled="isSendingFollow" @click.prevent="unFollowQuestion()"  size="mini" type="warning">取消关注</el-button>
        </el-tooltip>
        <el-tooltip v-else-if="this.itemHoverIndex==1&&!this.hasFollow" content="关注之后将获得更新提醒" placement="top">
          <el-button :loading="loadingFollowStatus" style="margin-right: 8px" :disabled="isSendingFollow" @click.prevent="followQuestion()"  size="mini" type="success">关注问题</el-button>
        </el-tooltip>
        发布于 <span style="padding-right: 5px">{{ questionOverview.publishDateTime | moment("YYYY-MM-DD ") }} </span>
      </div>
    </div>
    <div class="info">
      <div class="views">
        <el-tag type="gray"> {{ questionOverview.views }} 浏览</el-tag>
      </div>
      <div class="answers">
        <el-tag type="gray">{{ questionOverview.answers }}回答</el-tag>
      </div>
      <div class="solved-views">
        <el-tag v-if="questionOverview.solved" type="success">已解决</el-tag>
        <el-tag v-else type="gray">未解决</el-tag>
      </div>
    </div>
  </div>
</template>
<style scoped>
  .publisherInfo{
    float: left;
    margin-right: 10px;
    width: 75px;
    text-align: center;
    height: 100%;
    min-width: 50px;
    cursor: pointer;
  }
  .publisherInfo .avatar{
    width: 50px;
    height: 50px;
    margin: 0 auto;
    vertical-align: middle;
    display: block;
    border: green solid 1px;
  }
  .summary{
    padding: 5px;
    float: left;
    margin-right: 10px;
    width: 70%;
    min-height: 50px;
    border-right: solid grey 1px;
    border-left: solid grey 1px;
  }
  .summary h3{
    overflow: hidden;
  }
  .summary a {
    color: #38935f;
  }
  .summary .publish-date{
    clear: left;
    float: right;
  }
  .info{
    float: right;
    width: 15%;
  }
  .info>div {
    margin-bottom: 5px;
  }
</style>
<script>
  import { unFollowQuestion, followQuestion, hasFollowQuestion, unHiddenQuestion, hiddenQuestion } from '@/api/question'
  import '../../../static/UE/ueditor.parse'
  import { Message } from 'element-ui'
  import bus from '../../assets/eventBus.js'
  import { mapGetters } from 'vuex'
  import ElTag from "../../../node_modules/element-ui/packages/tag/src/tag.vue";
  export default {
    components: {ElTag},
    props: {
      questionOverview: Object
    },
    data () {
      return {
        isSendingFollow: false,
        itemHoverIndex: null,
        isSendingHidden: false,
        loadingHiddenStatus: false,
        hasFollow: true,
        hasHidden: false,
        loadingFollowStatus: true,
        tags: []
      }
    },
    created () {
      if(this.questionOverview.tags == '') {
        this.tags = []
      }else {
        this.tags = this.questionOverview.tags.split(',')
      }

    },
    computed: {
      ...mapGetters(['hasLogin', 'user']),
      isAdmin () {
        return this.hasLogin && this.user.roles[0].id==2
      },
      isUser () {
        return this.hasLogin && this.user.roles[0].id==1
      }
    },
    methods: {
      unFollowQuestion () {
        const questionId = this.questionOverview.id
        this.isSendingFollow = true
        let _this = this
        unFollowQuestion(questionId).then((response) => {
          if (response.result === true) {
            _this.hasFollow = false
            Message({
              message: '取消关注成功！',
              type: 'success',
              duration: 1000
            })
          }
          _this.isSendingFollow = false
        }).catch((e) => {
          Message({
            message: '取消关注失败，请稍后再试！',
            type: 'error',
            duration: 1000
          })
          _this.isSendingFollow = false
        })
      },
      followQuestion () {
        if (!this.hasLogin) {
          bus.$emit('requestLogin')
          return
        }
        const questionId = this.questionOverview.id
        this.isSendingFollow = true
        let _this = this
        followQuestion(questionId).then((response) => {
          if (response.result === true) {
            _this.hasFollow = true
            Message({
              message: '关注问题成功！',
              type: 'success',
              duration: 1000
            })
          }
          _this.isSendingFollow = false
        }).catch((e) => {
          Message({
            message: '关注问题失败，请稍后再试！',
            type: 'error',
            duration: 1000
          })
          _this.isSendingFollow = false
        })
      },
      unHiddenQuestion () {
        if (!this.hasLogin) {
          bus.$emit('requestLogin')
          return
        }
        const questionid = this.questionOverview.id
        this.isSendingHidden = true
        let _this = this
        unHiddenQuestion(questionid).then((response) => {
          if (response.result === true) {
            _this.hasHidden = false
            _this.questionOverview.hidden = false
            Message({
              message: '取消屏蔽成功！',
              type: 'success',
              duration: 1000
            })
          }
          _this.isSendingHidden = false
        }).catch((e) => {
          Message({
            message: '取消屏蔽失败，请稍后再试！',
            type: 'error',
            duration: 1000
          })
          _this.isSendingHidden = false
        })
      },
      hiddenQuestion () {
        if (!this.hasLogin) {
          bus.$emit('requestLogin')
          return
        }
        const questionid = this.questionOverview.id
        this.isSendingHidden = true
        let _this = this
        hiddenQuestion(questionid).then((response) => {
          if (response.result === true) {
            _this.hasHidden = true
            _this.questionOverview.hidden = true
            Message({
              message: '屏蔽问题成功！',
              type: 'success',
              duration: 1000
            })
          }
          _this.isSendingHidden = false
        }).catch((e) => {
          Message({
            message: '屏蔽问题失败，请稍后再试！',
            type: 'error',
            duration: 1000
          })
          _this.isSendingHidden = false
        })
      },
      getFollowStatus () {
        if (!this.hasLogin) {
          return
        }
        this.loadingFollowStatus = true
        const questionid = this.questionOverview.id
        let _this = this
        hasFollowQuestion(questionid).then((response) => {
          _this.hasFollow = response.result
          _this.loadingFollowStatus = false
        }).catch((e) => {
          Message({
            message: '获取信息失败，请稍后重试！',
            type: 'error',
            duration: 1000
          })
        })
      },
      mouseEnter () {
        this.itemHoverIndex = 1
        this.hasFollow = this.getFollowStatus()
        this.hasHidden = this.questionOverview.hidden
      },
      mouseLeave () {
        this.itemHoverIndex = null
      }
    }
  }
</script>
