<template>
  <textarea ref="textarea" />
</template>

<script>
import CodeMirror from 'codemirror'
import 'codemirror/lib/codemirror.css'
// 替换主题这里需修改名称
import 'codemirror/theme/idea.css'
import 'codemirror/mode/yaml/yaml'
export default {
  props: {
    value: {
      type: String,
      required: true
    },
    height: {
      type: String,
      required: false,
      default: '450px'
    },
    readOnly: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  data() {
    return {
      editor: false
    }
  },
  watch: {
    value(value) {
      const editorValue = this.editor.getValue()
      if (value !== editorValue) {
        this.editor.setValue(this.value)
      }
    },
    height(newHeight) {
      this.editor.setSize('auto', newHeight)
    }
  },
  mounted() {
    this.editor = CodeMirror.fromTextArea(this.$refs.textarea, {
      mode: 'text/x-yaml',
      lineNumbers: true,
      lint: true,
      lineWrapping: true,
      tabSize: 2,
      cursorHeight: 0.9,
      // 替换主题这里需修改名称
      theme: 'idea'
    })
    this.editor.setSize('auto', this.height)
    if (this.value) {
      this.editor.setValue(this.value)
    }
    this.editor.setOption('readOnly', this.readOnly)
    this.editor.on('change', cm => {
      this.$emit('changed', cm.getValue())
      this.$emit('input', cm.getValue())
    })
  },
  methods: {
    getValue() {
      return this.editor.getValue()
    }
  }
}
</script>
