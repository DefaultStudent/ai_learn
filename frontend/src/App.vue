<script setup lang="ts">
// 学习用单页入口：包含侧边导航、指标卡片、生产节拍和设备状态概览。
import { onMounted, ref } from "vue";
import { getDashboardSummary, type DashboardSummary } from "./api/dashboard";
const active = ref("dashboard");
const loading = ref(true);
const summary = ref<DashboardSummary>({
  activeOrders: 0,
  onlineDevices: 0,
  todayOutput: 0,
  qualityRate: 0,
});
const nav = [
  { key: "dashboard", label: "生产总览", icon: "▦" },
  { key: "orders", label: "生产工单", icon: "▤" },
  { key: "devices", label: "设备管理", icon: "◉" },
];
onMounted(async () => {
  try {
    summary.value = await getDashboardSummary();
  } finally {
    loading.value = false;
  }
});
</script>
<template>
  <div class="min-h-screen bg-slate-50">
    <aside
      class="fixed inset-y-0 left-0 z-10 hidden w-64 border-r border-slate-200 bg-white lg:block"
    >
      <div class="flex h-20 items-center gap-3 border-b border-slate-100 px-7">
        <div
          class="grid h-10 w-10 place-items-center rounded-xl bg-indigo-600 font-bold text-white"
        >
          M
        </div>
        <div>
          <p class="font-bold tracking-wide">MES FLOW</p>
          <p class="text-xs text-slate-400">学习型制造执行系统</p>
        </div>
      </div>
      <nav class="space-y-2 p-4">
        <button
          v-for="item in nav"
          :key="item.key"
          @click="active = item.key"
          :class="
            active === item.key
              ? 'bg-indigo-50 text-indigo-700'
              : 'text-slate-500 hover:bg-slate-50'
          "
          class="flex w-full items-center gap-3 rounded-xl px-4 py-3 text-left text-sm font-medium"
        >
          <span class="text-lg">{{ item.icon }}</span
          >{{ item.label }}
        </button>
      </nav>
      <div class="absolute bottom-0 w-full border-t border-slate-100 p-5">
        <p class="text-xs text-slate-400">系统状态</p>
        <div class="mt-2 flex items-center gap-2 text-sm">
          <span class="h-2 w-2 rounded-full bg-emerald-500"></span
          >基础服务运行正常
        </div>
      </div>
    </aside>
    <main class="lg:ml-64">
      <header
        class="flex h-20 items-center justify-between border-b border-slate-200 bg-white px-6 lg:px-10"
      >
        <div>
          <p class="text-sm text-slate-400">
            制造执行 / {{ nav.find((item) => item.key === active)?.label }}
          </p>
          <h1 class="text-xl font-bold">
            {{
              active === "dashboard"
                ? "生产运营总览"
                : nav.find((item) => item.key === active)?.label
            }}
          </h1>
        </div>
        <div class="flex items-center gap-3">
          <button
            class="rounded-lg border border-slate-200 px-3 py-2 text-sm text-slate-500"
          >
            帮助文档
          </button>
          <div
            class="grid h-9 w-9 place-items-center rounded-full bg-indigo-100 text-sm font-bold text-indigo-700"
          >
            学
          </div>
        </div>
      </header>
      <section class="space-y-7 p-6 lg:p-10">
        <div
          v-if="active !== 'dashboard'"
          class="rounded-2xl border border-dashed border-slate-300 bg-white p-10 text-center text-slate-500"
        >
          {{
            nav.find((item) => item.key === active)?.label
          }}模块正在建设中，API 与模块文档已预留。
        </div>
        <template v-else
          ><div class="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
            <div
              v-for="card in [
                {
                  label: '进行中工单',
                  value: summary.activeOrders,
                  unit: '张',
                  color: 'indigo',
                },
                {
                  label: '在线设备',
                  value: summary.onlineDevices,
                  unit: '台',
                  color: 'emerald',
                },
                {
                  label: '今日产量',
                  value: summary.todayOutput,
                  unit: '件',
                  color: 'amber',
                },
                {
                  label: '一次合格率',
                  value: summary.qualityRate,
                  unit: '%',
                  color: 'violet',
                },
              ]"
              :key="card.label"
              class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm"
            >
              <p class="text-sm text-slate-500">{{ card.label }}</p>
              <div class="mt-3 flex items-end gap-2">
                <span
                  class="text-3xl font-bold"
                  :class="`text-${card.color}-600`"
                  >{{ loading ? "—" : card.value }}</span
                ><span class="mb-1 text-sm text-slate-400">{{
                  card.unit
                }}</span>
              </div>
              <div class="mt-4 h-1.5 rounded-full bg-slate-100">
                <div
                  class="h-1.5 w-3/4 rounded-full"
                  :class="`bg-${card.color}-500`"
                ></div>
              </div>
            </div>
          </div>
          <div class="grid gap-6 xl:grid-cols-3">
            <div
              class="rounded-2xl border border-slate-200 bg-white p-6 xl:col-span-2"
            >
              <div class="flex items-center justify-between">
                <div>
                  <h2 class="font-bold">生产节拍</h2>
                  <p class="mt-1 text-sm text-slate-400">今日各时段完成数量</p>
                </div>
                <span
                  class="rounded-lg bg-emerald-50 px-3 py-1 text-xs font-medium text-emerald-600"
                  >实时</span
                >
              </div>
              <div
                class="mt-8 flex h-48 items-end gap-3 border-b border-slate-100 px-3"
              >
                <div
                  v-for="(height, index) in [
                    35, 55, 45, 72, 63, 88, 76, 94, 68, 82, 73, 90,
                  ]"
                  :key="index"
                  class="group flex flex-1 flex-col items-center gap-2"
                >
                  <div
                    class="w-full rounded-t-md bg-indigo-500 transition hover:bg-indigo-700"
                    :style="{ height: `${height}%` }"
                  ></div>
                  <span class="text-[10px] text-slate-400"
                    >{{ index + 8 }}:00</span
                  >
                </div>
              </div>
            </div>
            <div class="rounded-2xl border border-slate-200 bg-white p-6">
              <h2 class="font-bold">设备状态</h2>
              <div class="mt-6 space-y-5">
                <div
                  v-for="device in [
                    { name: '冲压线 A-01', status: '运行中', color: 'emerald' },
                    { name: '装配线 B-02', status: '待机', color: 'amber' },
                    { name: '检测台 C-03', status: '运行中', color: 'emerald' },
                    { name: '包装线 D-01', status: '维护中', color: 'rose' },
                  ]"
                  :key="device.name"
                  class="flex items-center justify-between"
                >
                  <div class="flex items-center gap-3">
                    <span
                      class="h-2.5 w-2.5 rounded-full"
                      :class="`bg-${device.color}-500`"
                    ></span
                    ><span class="text-sm">{{ device.name }}</span>
                  </div>
                  <span class="text-xs text-slate-400">{{
                    device.status
                  }}</span>
                </div>
              </div>
            </div>
          </div></template
        >
      </section>
    </main>
  </div>
</template>
