<template>
  <div class="app-container">
    <el-form ref="form" :model="form" label-width="120">
      <el-form-item label="选择模板">
        <el-select v-model="uid" placeholder="请选择">
          <el-option
            v-for="item in list"
            :key="item.uid"
            :label="item.name"
            :value="item.uid"
          />
        </el-select>
        <el-divider />
      </el-form-item>
      <el-row>
        <el-col :span="9">
          <div><img :src="form.base64Img" width="400px" class="avatar"></div>
          <el-form-item label="本地文件">
            <el-upload
              ref="upload"
              accept=".jpg,.png,.xlsx"
              name="imageFile"
              class="upload"
              :action="upload()"
              :on-preview="handlePreview"
              :on-change="handleChange"
              :on-remove="handleRemove"
              :on-success="handleSuccess"
              :before-upload="beforeUpload"
              ::limit="2"
              :multiple="true"
              :show-file-list="true"
              :auto-upload="false"
            >
              <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
              <el-button
                v-loading.fullscreen.lock="fullscreenLoading"
                style="margin-left: 10px;"
                type="success"
                size="small"
                element-loading-text="加载中"
                @click="submitUpload"
              >识别</el-button>
              <div slot="tip" class="el-upload__tip">Excel format: xlsx, Image format: JPG(JPEG), PNG</div>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="">
            <json-viewer
              :value="form.result2"
              :expand-depth="4"
              copyable
              width="400px"
            />
          </el-form-item>
        </el-col>
      </el-row>

    </el-form>
  </div>
</template>

<script>
import { infoForImageUrl, getTemplates } from '@/api/template'

import JsonViewer from 'vue-json-viewer'

export default {
  name: 'InferenceDetail',
  components: {
    JsonViewer
  },
  data() {
    return {
      fullscreenLoading: false,
      list: null,
      uid: '',
      form: {
        result1: '',
        result2: '',
        base64Img: ''
      }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      getTemplates().then(response => {
        this.list = response.data.result
      }).catch(function(response) {
        console.log(response)// 发生错误时执行的代码
      })
    },
    upload() {
      return `${process.env.VUE_APP_BASE_API}/template/infoForImageFile` + '?uid=' + this.uid
    },
    submitUpload() {
      if (this.uid === '') {
        this.$message.error('请选择模板!')
      } else {
        this.fullscreenLoading = true
        this.$refs.upload.submit()
      }
    },
    handleRemove(file, fileList) {
      console.log(file, fileList)
    },
    handleChange(file) {
      console.log(file)
    },
    handlePreview(file) {
      console.log(file)
    },
    handleSuccess(file) {
      this.form.base64Img = file.data.base64Img
      this.form.result2 = file.data.result
      this.fullscreenLoading = false
    },
    beforeUpload(file) {
      const pass = file.type === 'image/jpg' || 'image/jpeg' || 'image/png'
      if (!pass) {
        this.$message.error('Image format should be JPG(JPEG) or PNG!')
      }
      if (this.uid === '') {
        this.$message.error('请选择模板!')
        return false
      }
      return pass
    },
    onSubmit() {
      if (this.uid === '') {
        this.$message.error('请选择模板!')
      } else {
        this.fullscreenLoading = true
        infoForImageUrl(this.uid, this.form).then(response => {
          this.fullscreenLoading = false
          this.form.result1 = response.data.result
        })
      }
    }
  }
}
</script>

<style scoped>
  .el-input {
    width: 600px;
  }

  .input-with-select .el-input-group__prepend {
    background-color: #fff;
  }

  .line {
    text-align: center;
  }
</style>
