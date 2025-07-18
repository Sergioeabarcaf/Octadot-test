<template>
  <div>
    <header class="app-header">
      <h1>Route Optimizer</h1>
      <p>Upload your CSV and calculate the fastest route between locations.</p>
    </header>
    <div class="app-container">
      <FileUploadPanel @uploaded="onUploaded" />
      <RouteCalculatorPanel :enabled="csvUploaded" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import FileUploadPanel from './components/FileUploadPanel.vue'
import RouteCalculatorPanel from './components/RouteCalculatorPanel.vue'

const csvUploaded = ref(false)
function onUploaded() {
  csvUploaded.value = true
}
</script>

<style src="./style.css"></style> 