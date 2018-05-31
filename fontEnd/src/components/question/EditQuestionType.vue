<template>
  <div class="components-container">
    <div id="question-panel" style="height: 200%">
      <el-form label-width="80px">
        <el-form-item label="所属科目">
          <el-select v-model="subject" placeholder="请选择" @change="getCourseList" style="width: 40%">
            <el-option
              v-for="group in groupQuestionType"
              :key="group.label"
              :label="group.label"
              :value="group.label">
              </el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="questionType" label="问题类型">
          <el-select v-model="typeId" placeholder="请选择" style="width: 40%" v-on:change="selectType">
            <el-option
              v-for="option in typeOption"
              :key="option.value"
              :label="option.label"
              :value="option.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <h3 style="position: relative;right: -7px;color: #111111">修改为</h3>
      <el-form label-width="80px" style="position: relative;top: 18px;">
      <el-form-item label="所属科目" style="width: 44.1%">
        <el-input v-model="newSubject"></el-input>
      </el-form-item>
      <el-form-item label="问题类型" style="width: 44.1%">
        <el-input v-model="newCourse"></el-input>
      </el-form-item>
      </el-form>
      <el-button :disabled="isSendingRequest" style=" margin-top: 15px;margin-left: 475px"  size="small" type="success"  @click="modifyType()" >
        确定</el-button>
    </div>
  </div>

</template>
<style scoped>
  .components-container{
    margin-left: 5%;
    margin-right: 5%;
    width: 90%;
  }
  .components-container{
    margin-top: 10px;
  }
</style>
<script>
  import UE from '../UE.vue'
  import { modifyQuestionType } from '@/api/questionType'
  import { getQuestionType } from '@/api/question'
  import { Message } from 'element-ui'
  import $ from 'jquery'
  import { transformQuestionType2Map } from '@/utils/util'
  export default {
    components: {UE},
    data () {
      return {
        subject: '',
        typeId: '',
        newSubject: '',
        newCourse: '',
        defaultMsg: '',
        typeOption: [],
        questionTypeArray: [],
        config: {
          initialFrameWidth: null,
          initialFrameHeight: 250
        },
        isSendingRequest: false
      }
    },
    computed: {
      groupQuestionType () {
        const questionTypeMap = transformQuestionType2Map(this.questionTypeArray)
        let options = []
        for (var key in questionTypeMap) {
          let option = []
          let typeArray = questionTypeMap[key]
          for (var i = 0; i < typeArray.length; i++) {
            option.push({
              label: typeArray[i]['course'],
              value: '' + typeArray[i]['id']
            })
          }
          options.push({
            label: key,
            options: option
          })
        }
        return options
      }
    },
    mounted: function () {
      let _this = this
      getQuestionType().then((response) => {
        if (response.status === '200') {
          _this.questionTypeArray = response.result
        }
      })
    },
    methods: {
      getCourseList () {
        this.typeOption = []
        for (var i = 0; i < this.groupQuestionType.length; i++) {
          if (this.subject == this.groupQuestionType[i]['label']) {
            this.typeOption = this.groupQuestionType[i]['options']
          }
        }
        this.typeId=''
      },
      selectType (){
        console.log(this.typeId)
      },
      isNewType () {
        for (var i = 0; i < this.groupQuestionType.length; i++) {
          if (this.newSubject == this.groupQuestionType[i]['label']) {
            for (var j = 0; j < this.groupQuestionType[i]['options'].length; j++) {
              if (this.newCourse == this.groupQuestionType[i]['options'][j]['label']) {
                return false
              }
            }
          }
        }
        return true
      },
      resetFields () {
        let editor = this.$refs.ue
        editor.clearContent()
        this.questionForm.questionTitle = ''
        this.questionForm.questionType = ''
      },
      modifyType () {
        this.isSendingRequest = true
        if (!this.newSubject || !this.newCourse) {
          Message({
            message: '问题类型或所属科目不能为空',
            type: 'error',
            duration: 1 * 1000
          })
          this.isSendingRequest = false
          this.subject = ''
          this.typeId = ''
          this.newSubject = ''
          this.newCourse = ''
          return
        }
        else if (!this.isNewType()) {
          Message({
            message: '此类型已存在！',
            type: 'error',
            duration: 1 * 1000
          })
          this.isSendingRequest = false
          this.subject = ''
          this.typeId = ''
          this.newSubject = ''
          this.newCourse = ''
          return
        }
        else {
          let _this = this
          modifyQuestionType(this.typeId, this.newCourse, this.newSubject)
            .then((response) => {
              if (response.status === '200') {
                Message({
                  message: '类型修改成功！',
                  type: 'success',
                  duration: 1 * 1000
                })
                _this.isSendingRequest = false
                _this.subject = ''
                _this.typeId = ''
                _this.newSubject = ''
                _this.newCourse = ''
                window.location.reload()
                return
              }
            }).catch((e) => {
            Message({
              message: '类型修改失败，请稍后再试',
              type: 'error',
              duration: 1 * 1000
            })
            _this.isSendingRequest = false
            _this.subject = ''
            _this.typeId = ''
            _this.newSubject = ''
            _this.newCourse = ''
            return
          })
          this.isSendingRequest = false
          this.subject = ''
          this.typeId = ''
          this.newSubject = ''
          this.newCourse = ''
          return
        }
      }
    }
  }
</script>
