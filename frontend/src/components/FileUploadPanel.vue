<template>
  <div class="panel">
    <h2>Upload CSV File</h2>
    <div
      class="drag-area"
      :class="{ dragover: isDragOver }"
      @dragover.prevent="onDragOver"
      @dragleave.prevent="onDragLeave"
      @drop.prevent="onDrop"
      @click="onClickInput"
    >
      <p v-if="!fileName">Drag & drop your CSV file here, or click to select</p>
      <p v-else>Selected file: <strong>{{ fileName }}</strong></p>
    </div>
    <input
      ref="fileInput"
      type="file"
      accept=".csv"
      style="display: none"
      @change="onFileChange"
    />
    <button :disabled="loading" @click="onUpload" v-if="file">Upload</button>
    <div v-if="error" style="color: #dc2626;">{{ error }}</div>
    <div v-if="success" style="color: #16a34a;">File uploaded successfully!</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const emit = defineEmits(['uploaded'])

const file = ref(null)
const fileName = ref('')
const isDragOver = ref(false)
const loading = ref(false)
const error = ref('')
const success = ref(false)
const fileInput = ref(null)

function onDragOver() {
  isDragOver.value = true
}
function onDragLeave() {
  isDragOver.value = false
}
function onDrop(e) {
  isDragOver.value = false
  const dropped = e.dataTransfer.files[0]
  if (dropped && dropped.type === 'text/csv') {
    file.value = dropped
    fileName.value = dropped.name
    error.value = ''
    success.value = false
  } else {
    error.value = 'Please upload a valid CSV file.'
    file.value = null
    fileName.value = ''
    success.value = false
  }
}
function onClickInput() {
  fileInput.value.click()
}
function onFileChange(e) {
  const selected = e.target.files[0]
  if (selected && selected.type === 'text/csv') {
    file.value = selected
    fileName.value = selected.name
    error.value = ''
    success.value = false
  } else {
    error.value = 'Please upload a valid CSV file.'
    file.value = null
    fileName.value = ''
    success.value = false
  }
}
async function onUpload() {
  if (!file.value) return
  loading.value = true
  error.value = ''
  success.value = false
  try {
    const formData = new FormData()
    formData.append('file', file.value)
    const apiUrl = import.meta.env.VITE_API_URL || '/api';
    await axios.post(`${apiUrl}/connections/upload`, formData)
    success.value = true
    emit('uploaded')
  } catch (err) {
    error.value = err?.response?.data || 'Upload failed.'
  } finally {
    loading.value = false
  }
}
</script> 