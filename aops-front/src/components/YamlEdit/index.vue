<template>
  <textarea ref="textarea" />
</template>

<script>
import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/css/css.js'
import 'codemirror/mode/yaml/yaml.js'
import 'codemirror/mode/yaml-frontmatter/yaml-frontmatter.js'
import 'codemirror/mode/javascript/javascript'
import 'codemirror/addon/selection/active-line' // 代码高亮
import 'codemirror/addon/fold/foldgutter.css'
import 'codemirror/addon/fold/foldcode'
import 'codemirror/addon/fold/brace-fold'
import 'codemirror/addon/scroll/simplescrollbars.css'
import 'codemirror/addon/scroll/simplescrollbars'
import 'codemirror/addon/hint/show-hint'
import 'codemirror/addon/hint/javascript-hint'
import 'codemirror/addon/hint/anyword-hint'
import 'codemirror/addon/hint/css-hint'
import 'codemirror/addon/hint/show-hint.css'
import 'codemirror/theme/dracula.css'
import 'codemirror/theme/monokai.css' // 主题
import CodeMirror from 'codemirror'
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
      mode: 'yaml',
      lineNumbers: true, // 显示行数
      lint: true,
      matchBrackets: true, // 括号匹配
      lineWrapping: true, // 自动换行
      indentUnit: 2, // 缩进单位为2
      smartIndent: true, // 自动缩进是否开启
      styleActiveLine: true, // 当前行背景高亮
      tabSize: 2,
      cursorHeight: 1,
      theme: 'dracula' // 主题
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
