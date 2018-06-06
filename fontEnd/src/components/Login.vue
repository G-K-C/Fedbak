<template>
  <div id = "wrap-banner">
    <div id="loginContainer">
      <el-card class="box-card">
        <div slot="header" class="clearfix login-title">
          <span style="">遇见知识</span>
        </div>
        <div id="login-form" v-if="!register">
          <el-form ref="loginform" :model="loginform" :rules="loginrule">
            <el-form-item prop="username">
              <el-input icon="fa-user-circle" type="text" v-model="loginform.username" autoComplete="false"
                        autofocus placeholder="请输入账号" ></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input type="password" v-model="loginform.password" autoComplete="false"
                        icon="fa-lock" placeholder="请输入密码"></el-input>
            </el-form-item>
            <el-form-item class="verification">
              <div id="login_verification_code_id" style="width: 400px"></div>
            </el-form-item>
            <el-form-item class="login-button">
              <el-button style="width: 100%" type="success" :loading="logining" @click.native.prevent="login" >登录</el-button>
            </el-form-item>
          </el-form>
          <router-link to="/login?register=2">
          <span  style="float: left; margin-top: -20px">
            <i class="fa fa-address-book-o"></i> 忘记密码?
          </span>
          </router-link>
          <router-link to="/login?register=1">
          <span  style="float: right; margin-top: -20px">
            <i class="fa fa-address-book"></i> 去注册
          </span>
          </router-link>
        </div>
        <div id="register-form" v-else-if="register === 1">
          <!-- 此段必须要引入 -->
          <div id="_umfp" style="display:inline;width:1px;height:1px;overflow:hidden"></div>
          <el-form ref="registerform" :model="registerform" :rules="registerrule">
            <el-form-item prop="loginUsername">
              <el-input icon="fa-user-circle" type="text" v-model="registerform.loginUsername" autoComplete="false"
                        autofocus placeholder="请输入账号" ></el-input>
            </el-form-item>
            <el-form-item prop="username">
              <el-input icon="fa-user-o" type="text" v-model="registerform.username" autoComplete="false"
                        placeholder="请输入用户名" ></el-input>
            </el-form-item>
            <el-form-item prop="mailbox">
              <el-input icon="fa-envelope-o" type="text" v-model="registerform.email" autoComplete="false"
                        placeholder="请输入邮箱" ></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input icon="fa-lock" type="password" v-model="registerform.password" autoComplete="false"
                        placeholder="请输入密码" ></el-input>
            </el-form-item>
            <el-form-item prop="passwordAgain">
              <el-input icon="fa-key" type="password" v-model="registerform.passwordAgain" autoComplete="false"
                        placeholder="请再一次输入密码" ></el-input>
            </el-form-item>
            <el-form-item class="verification">
              <div id="verification_code_id" style="width: 400px"></div>
            </el-form-item>
            <el-form-item class="register-button">
              <el-button style="width: 100%" type="success" :loading="registering" @click.native.prevent="userRegister" >注册</el-button>
            </el-form-item>
          </el-form>
          <router-link @click="register=0" to="/login">
          <span  style="float: right; margin-top: -20px">
            <i class="fa fa-user-o"></i> 去登录
          </span>
          </router-link>
        </div>
        <div id="forget-form" v-else-if="register === 2">
          <el-form ref="forgetform" :model="forgetform" :rules="forgetrule">
            <el-form-item prop="mailbox">
              <el-input icon="fa-envelope-o" type="text" v-model="forgetform.mailbox" autoComplete="false"
                        placeholder="请输入注册邮箱" style="width:250px;float:left">
              </el-input>
              <el-button style="width:50px;height:36px;float:right;margin:0;padding:0" type="success" :disabled="disableSend" :loading="sending" @click.native.prevent="sendMail" >{{sendContent}}</el-button>
            </el-form-item>
            <el-form-item prop="password">
              <el-input icon="fa-lock" type="text" v-model="forgetform.password" autoComplete="false"
                        placeholder="请输入新密码" ></el-input>
            </el-form-item>
            <el-form-item prop="passwordAgain">
              <el-input icon="fa-key" type="password" v-model="forgetform.passwordAgain" autoComplete="false"
                        placeholder="请再一次输入密码" ></el-input>
            </el-form-item>
            <el-form-item prop="verificationCode">
              <el-input icon="fa-telegram" type="text" v-model="forgetform.verificationCode" autoComplete="false"
                        placeholder="请输入验证码" ></el-input>
            </el-form-item>
            <el-form-item class="verification">
              <div id="forget_verification_code_id" style="width: 400px"></div>
            </el-form-item>
            <el-form-item class="forget-button">
              <el-button style="width: 100%" type="success" :loading="resetting" @click.native.prevent="resetPassword" >确认重置</el-button>
            </el-form-item>
          </el-form>
          <router-link @click="register=0" to="/login">
          <span  style="float: right; margin-top: -20px">
            <i class="fa fa-user-o"></i> 去登录
          </span>
          </router-link>
        </div>
      </el-card>
    </div>
    <vue-particles
      color="#dedede"
      :particleOpacity="0.7"
      :particlesNumber="80"
      shapeType="circle"
      :particleSize="4"
      linesColor="#dedede"
      :linesWidth="1"
      :lineLinked="true"
      :lineOpacity="0.4"
      :linesDistance="150"
      :moveSpeed="3"
      :hoverEffect="true"
      hoverMode="grab"
      :clickEffect="true"
      clickMode="push">
    </vue-particles>
  </div>
</template>
<script>
  import '../../static/js/nc'
  import { validateLoginUsername, register , validateMailbox,send,reset} from '@/api/login'
  import { Message } from 'element-ui'
  /* eslint-disable new-cap,camelcase,no-undef */
  export default {
    data () {
      const validatePass2 = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请再次输入密码'))
        } else if (value !== this.registerform.password) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      }
      const validateNewPass2 = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请再次输入密码'))
        } else if (value !== this.forgetform.password) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      }
      const validateloginUserName = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请输入账号!'))
        } else {
          validateLoginUsername(value).then((response) => {
            if (response.status === '200') {
              if (response.result === false) {
                callback(new Error('该账号已经存在，请尝试新的账号!'))
              }
            }
            callback()
          })
        }
      }
      const validatemailbox = (rule, value, callback) => {
        let mailReg = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
        if (!mailReg.test(value)) {
          callback(new Error('邮箱格式不正确'))
        } else {
          validateMailbox(value).then((response) => {
            if (response.status === '200') {
              if (response.result === true) {
                callback(new Error('该邮箱不存在，请确认邮箱是否输入正确!'))
              }
            }
            callback()
          })
        }
      }
      return {
        loginform: {
          username: '',
          password: ''
        },
        loginrule: {
          username: [
            { required: true, message: '请输入账号', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' }
          ]
        },
        logining: false,
        register: 0,
        forget: false,
        registerform: {
          loginUsername: '',
          username: '',
          email: '',
          password: '',
          passwordAgain: ''
        },
        registerrule: {
          loginUsername: [
            { validator: validateloginUserName, trigger: 'blur' }
          ],
          username: [
            { required: true, message: '请输入用户名', trigger: 'blur' }
          ],
          mailbox: [
            { validator: validatemailbox, trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' }
          ],
          passwordAgain: [
            { validator: validatePass2, trigger: 'blur' }
          ]
        },
        registering: false,
        registerVerification: false,
        loginVerification: false,
        forgetVerification: false,
        forgetform: {
          mailbox: '',
          password: '',
          passwordAgain: '',
          verificationCode: ''
        },
        forgetrule: {
          mailbox: [
            { validator: validatemailbox, trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请输入新密码', trigger: 'blur' }
          ],
          passwordAgain: [
            { validator: validateNewPass2, trigger: 'blur' }
          ],
          verificationCode: [
            { required: true, message: '请输入验证码', trigger: 'blur' }
          ]
        },
        resetting: false,
        sending: false,
        disableSend: false,
        sendContent: '发送'
      }
    },
    methods: {
      login (ev) {
        if (!this.loginVerification) {
          Message({
            message: '请滑动滑块进行验证！',
            type: 'error',
            duration: 2000
          })
          return
        }
        let _this = this
        this.$refs.loginform.validate((valid) => {
          if (valid) {
            _this.logining = true
            _this.$store.dispatch('login', _this.loginform)
              .then(() => {
                _this.logining = false
                _this.$router.push({ path: '/' })
              }).catch(() => {
              _this.logining = false
            })
          }
        })
      },
      userRegister (ev) {
        if (!this.registerVerification) {
          Message({
            message: '请滑动滑块进行验证！',
            type: 'error',
            duration: 2000
          })
          return
        }
        let _this = this
        this.$refs.registerform.validate((valid) => {
          if (valid) {
            _this.registering = true
            register(_this.registerform.loginUsername, _this.registerform.username, _this.registerform.email, _this.registerform.password).then((response) => {
              if (response.status === '201') {
                Message({
                  message: '注册成功!',
                  type: 'success',
                  duration: 5000
                })
                _this.$refs.registerform.resetFields()
              } else {
                Message({
                  message: '注册失败，请稍后重试!',
                  type: 'error',
                  duration: 1000
                })
              }
              _this.registering = false
            }).catch((e) => {
              Message({
                message: '注册失败，请稍后重试!',
                type: 'error',
                duration: 1000
              })
              _this.registering = false
            })
          }
        })
      },
      sendMail (ev) {
        let _this = this
        this.$refs.forgetform.validate((valid) => {
          if (!valid) {
            _this.disableSend = true
            _this.sending = true
            send(_this.forgetform.mailbox).then((response) => {
              if (response.status === '201') {
                Message({
                  message: '发送成功!',
                  type: 'success',
                  duration: 5000
                })
                _this.$refs.forgetform.resetFields()
              } else {
                Message({
                  message: '发送失败，请稍后重试!',
                  type: 'error',
                  duration: 1000
                })
              }
              _this.sending = false
            }).catch((e) => {
              Message({
                message: '发送失败，请稍后重试!',
                type: 'error',
                duration: 1000
              })
              _this.sending = false
            })
            let sec =60;
            for(let  i=0; i<=60; i++){
              window.setTimeout(function(){
                if (sec !== 0) {
                  _this.sendContent =   '(' + sec + ')' ;
                  sec--;
                } else {
                  _this.sendContent = '发送'
                  _this.disableSend = false
                }
              }, i * 1000)
            }
          } else{
            Message({
              message: '请输入正确邮箱！',
              type: 'error',
              duration: 1000
            })
          }
        })
      },
      resetPassword (ev) {
        if (!this.forgetVerification) {
          Message({
            message: '请滑动滑块进行验证！',
            type: 'error',
            duration: 2000
          })
          return
        }
        let _this = this
        this.$refs.forgetform.validate((valid) => {
          if (valid) {
            _this.resetting = true
            reset(_this.forgetform.mailbox, _this.forgetform.password, _this.forgetform.verificationCode).then((response) => {
              if (response.status === '201') {
                Message({
                  message: '重置成功!',
                  type: 'success',
                  duration: 5000
                })
                _this.$refs.registerform.resetFields()
              } else {
                Message({
                  message: '重置失败，请稍后重试!',
                  type: 'error',
                  duration: 1000
                })
              }
              _this.registering = false
            })
          }
        })
      },
      resetRegisterForm () {
        const _this = this
        this.$nextTick(function () {
          _this.$refs.registerform.resetFields()
          _this.registerform.password = ''
          _this.registerform.passwordAgain = ''
          _this.initVerificationCode('verification_code_id', function (data) {
            _this.registerVerification = true
          })
          this.registerVerification = false
        })
      },
      resetLoginForm () {
        const _this = this
        this.$nextTick(function () {
          _this.$refs.loginform.resetFields()
          _this.initVerificationCode('login_verification_code_id', function (data) {
            _this.loginVerification = true
          })
        })
      },
      resetForgetForm () {
        const _this = this
        this.$nextTick(function () {
          _this.$refs.forgetform.resetFields()
          _this.forgetform.password = ''
          _this.forgetform.passwordAgain = ''
          _this.forgetform.verificationCode = ''
          _this.initVerificationCode('forget_verification_code_id', function (data) {
            _this.forgetVerification = true
          })
        })
      },
      initVerificationCode (id, callback) {
        const nc = new noCaptcha()
        const nc_appkey = 'FFFF0000000001787D7C'  // 应用标识,不可更改
        const nc_scene = 'register'  // 场景,不可更改
        const nc_token = [nc_appkey, (new Date()).getTime(), Math.random()].join(':')
        const nc_option = {
          renderTo: '#' + id, // 渲染到该DOM ID指定的Div位置
          appkey: nc_appkey,
          scene: nc_scene,
          token: nc_token,
          trans: '{"name1":"code100"}', // 测试用，特殊nc_appkey时才生效，正式上线时请务必要删除；code0:通过;code100:点击验证码;code200:图形验证码;code300:恶意请求拦截处理
          callback: callback
        }
        nc.init(nc_option)
      }
    },
    mounted: function () {
      this.register = parseInt(this.$route.query.register)
      if (this.register === 1) {
        this.resetRegisterForm()
      } else if (this.register === 2){
        this.resetForgetForm()
      } else {
        this.resetLoginForm()
      }
    },
    watch: {
      '$route' (to, from) {
        this.register = parseInt(this.$route.query.register)
        if (this.register === 1) {
          this.resetRegisterForm()
        } else if (this.register === 2){
          this.resetForgetForm()
        } else {
          this.resetLoginForm()
        }
      }
    }
  }
</script>
<style scoped>
  @import '../../static/css/nc.css';
  #loginContainer{
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin: 123px auto;
    width: 350px;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
    z-index: 1;
  }
  #loginContainer button span{
    color: white;
    font-size: 20px;
    line-height: 1;
  }
  .login-title{
    text-align: center;
  }
  #loginContainer .login-title span{
    font-family: "Arial","Microsoft YaHei","黑体","宋体",sans-serif;
    color: dodgerblue ;
    font-size: 2em ;
    line-height: 36px;
  }
  #wrap-banner {
    position: relative;
    -webkit-align-items: center;
    -ms-flex-align: center;
    align-items: center;
    display: -webkit-flex;
    display: flex;
    height: 100%;
    justify-content: center;
  }
  #particles-js {
    background-image: url("../assets/sky.jpg");
    background-size: cover;
    position: absolute;
    top: 1%;
    left: 0;
    width: 100%;
    height: 100%;
  }
</style>
