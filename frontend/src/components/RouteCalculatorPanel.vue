<template>
  <div class="panel" :style="disabled ? 'opacity:0.5;pointer-events:none;' : ''">
    <h2>Shortest Route Calculator</h2>
    <form @submit.prevent="onCalculate" class="form-shortest-route">
    <div class="form-group">
    <label for="from">From:</label>
      <input id="from" v-model="from" list="locations" placeholder="Origin" required />
    </div>
    <div class="form-group">
    <label for="to">To:</label>
      <input id="to" v-model="to" list="locations" placeholder="Destination" required />
      <datalist id="locations">
        <option v-for="loc in locations" :key="loc" :value="loc" />
      </datalist>
    </div>
      <button type="submit" :disabled="loading">Calculate</button>
    </form>
    <div v-if="error" style="color: #dc2626;">{{ error }}</div>
    <div v-if="route && route.length" class="route-result">
      <div><strong>Route:</strong> {{ route.join(' → ') }}</div>
      <div><strong>Total time:</strong> {{ totalTime }} min</div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps({
  enabled: Boolean
})

const disabled = ref(!props.enabled)
const from = ref('')
const to = ref('')
const locations = ref([])
const route = ref([])
const totalTime = ref(0)
const error = ref('')
const loading = ref(false)

watch(() => props.enabled, (val) => {
  disabled.value = !val
  if (val) fetchLocations()
})

onMounted(() => {
  if (props.enabled) fetchLocations()
})

async function fetchLocations() {
  // Obtenemos las ubicaciones desde el endpoint de conexiones
  try {
    const apiUrl = import.meta.env.VITE_API_URL || '/api';
    const res = await axios.get(`${apiUrl}/connections/list`)
    // Extraer ubicaciones del texto de respuesta
    const match = res.data.match(/\[(.*?)\]/)
    if (match && match[1]) {
      locations.value = match[1].split(',').map(s => s.trim())
    }
  } catch (e) {
    locations.value = []
  }
}

async function onCalculate() {
  error.value = ''
  route.value = []
  totalTime.value = 0
  if (!from.value || !to.value) return
  loading.value = true
  try {
    const apiUrl = import.meta.env.VITE_API_URL || '/api';
    const res = await axios.get(`${apiUrl}/routes/shortest`, {
      params: { from: from.value, to: to.value }
    })
    route.value = res.data.route
    totalTime.value = res.data.totalTime
  } catch (err) {
    error.value = err?.response?.data || 'No route found.'
  } finally {
    loading.value = false
  }
}
</script> 