<template>
  <div class="device-list-container">
    <a-typography-title :level="2">设备列表</a-typography-title>
    <a-typography-paragraph>这里是您所有设备的列表。</a-typography-paragraph>
    <a-list item-layout="horizontal" :data-source="devices">
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta>
            <template #title>
              <a>{{ item.name }}</a>
            </template>
          </a-list-item-meta>
          <template #actions>
            <a-tag :color="item.status === 'online' ? 'success' : 'error'">
              {{ item.status === 'online' ? '在线' : '离线' }}
            </a-tag>
          </template>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const devices = ref([
  { id: 1, name: '智能灯泡', status: 'online' },
  { id: 2, name: '安全摄像头', status: 'offline' },
  { id: 3, name: '温度传感器', status: 'online' },
  { id: 4, name: '智能插座', status: 'online' },
]);
</script>

<style scoped>
.device-list-container {
  background-color: white;
  padding: 2rem;
  border-radius: 0.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}
</style>
