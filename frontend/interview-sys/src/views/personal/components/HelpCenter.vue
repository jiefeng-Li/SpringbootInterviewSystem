<template>
  <el-collapse
    :before-collapse="
      () => {
        return false;
      }
    "
  >
    <el-collapse-item
      title="注销账号"
      name="1"
      @click="console.log('Item 1 clicked')"
    >
    </el-collapse-item>
    <el-collapse-item
      title="系统反馈"
      name="2"
      @click="console.log('Item 2 clicked')"
    >
    </el-collapse-item>
    <el-collapse-item
      title="联系客服"
      name="3"
      @click="console.log('Item 3 clicked')"
    >
    </el-collapse-item>
  </el-collapse>
</template>

<script></script>

<style lang="scss" scoped></style>
