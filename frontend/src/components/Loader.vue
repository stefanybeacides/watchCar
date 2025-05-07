<!-- components/Loader.vue -->
<template>
  <div class="loader-overlay" v-if="visible">
    <div class="spinner"></div>
  </div>
</template>

<script>
export default {
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
}
</script>

<style scoped>
.loader-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}
.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #2e7d32;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
