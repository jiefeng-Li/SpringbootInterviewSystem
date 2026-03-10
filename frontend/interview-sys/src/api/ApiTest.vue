<template>
  <div>
    <h1>API Test</h1>
    <el-button @click="testApi">Test API</el-button>
  </div>
</template>

<script setup>
import { getOneUser } from "@/api/user.js";
import { ref } from "vue";

const data = ref({
  username: "sysadmin",
  phone: "18884323684",
});

async function testApi() {
  console.log("Testing API with data:", data.value);
  try {
    const res = await getOneUser(data.value);
    console.log("API Response:", res);
  } catch (error) {
    console.error("API Error:", error);
  }
}
</script>

<style lang="scss" scoped></style>
