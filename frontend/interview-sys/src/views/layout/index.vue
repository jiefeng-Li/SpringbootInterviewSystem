<template>
  <el-container class="base-container">
    <el-header style="text-align: right; font-size: 12px">
      <slot name="header"></slot>
    </el-header>
    <el-container>
      <el-aside :width="asideWidth" :style="{ background: asideBackground }">
        <slot name="aside"></slot>
      </el-aside>
      <el-main :style="{ background: mainBackground }">
        <slot name="main"></slot>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
const props = defineProps({
  asideWidth: {
    type: String,
    default: "200px",
  },
  asideBackground: {
    type: String,
    default: "rgb(217, 236, 255)",
  },
  mainBackground: {
    type: String,
    default: "#f5f5f5",
  },
});
</script>

<style lang="scss" scoped>
.base-container {
  .el-header {
    height: 4em;
    position: relative;
    background-color: #409eff;
    color: var(--el-text-color-primary);
    display: flex;
    justify-content: flex-end;
  }
}
</style>
