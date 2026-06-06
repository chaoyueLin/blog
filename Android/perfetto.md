cat config.pbtx | adb shell perfetto -c - --txt -o /data/misc/perfetto-traces/trace.pftrace

adb pull /data/misc/perfetto-traces/trace.pftrace

config.pbtx
...
buffers {
  size_kb: 65536
  fill_policy: DISCARD
}
data_sources {
  config {
    name: "android.heapprofd"
    heapprofd_config {
      sampling_interval_bytes: 4096
      process_cmdline: "com.example.memory.test"
      shmem_size_bytes: 8388608
      block_client: true
      all_heaps: false
      heaps: "com.android.art"
    }
  }
}
duration_ms: 10000
...


config_cpu.pbtx

...
buffers: {
    size_kb: 522240
    fill_policy: RING_BUFFER
}
buffers: {
    size_kb: 2048
    fill_policy: RING_BUFFER
}
data_sources: {
    config {
        name: "android.gpu.memory"
    }
}
data_sources: {
    config {
        name: "linux.process_stats"
        target_buffer: 1
        process_stats_config {
            scan_all_processes_on_start: true
        }
    }
}
data_sources: {
    config {
        name: "android.log"
        android_log_config {
            log_ids: LID_DEFAULT
            log_ids: LID_RADIO
            log_ids: LID_EVENTS
            log_ids: LID_SYSTEM
            log_ids: LID_CRASH
            log_ids: LID_STATS
            log_ids: LID_SECURITY
            log_ids: LID_KERNEL
        }
    }
}
data_sources: {
    config {
        name: "android.surfaceflinger.frametimeline"
    }
}
data_sources: {
    config {
        name: "org.chromium.trace_event"
        chrome_config {
            trace_config: "{\"record_mode\":\"record-until-full\",\"included_categories\":[\"toplevel\",\"cc\",\"gpu\",\"viz\",\"ui\",\"views\",\"benchmark\",\"evdev\",\"input\",\"disabled-by-default-toplevel.flow\"],\"memory_dump_config\":{}}"
        }
    }
}
data_sources: {
    config {
        name: "org.chromium.trace_metadata"
        chrome_config {
            trace_config: "{\"record_mode\":\"record-until-full\",\"included_categories\":[\"toplevel\",\"cc\",\"gpu\",\"viz\",\"ui\",\"views\",\"benchmark\",\"evdev\",\"input\",\"disabled-by-default-toplevel.flow\"],\"memory_dump_config\":{}}"
        }
    }
}
data_sources: {
    config {
        name: "linux.sys_stats"
        sys_stats_config {
            stat_period_ms: 1000
            stat_counters: STAT_CPU_TIMES
            stat_counters: STAT_FORK_COUNT
        }
    }
}
data_sources: {
    config {
        name: "linux.ftrace"
        ftrace_config {
            ftrace_events: "sched/sched_switch"
            ftrace_events: "power/suspend_resume"
            ftrace_events: "sched/sched_wakeup"
            ftrace_events: "sched/sched_wakeup_new"
            ftrace_events: "sched/sched_waking"
            ftrace_events: "power/cpu_frequency"
            ftrace_events: "power/cpu_idle"
            ftrace_events: "power/gpu_frequency"
            ftrace_events: "gpu_mem/gpu_mem_total"
            ftrace_events: "raw_syscalls/sys_enter"
            ftrace_events: "raw_syscalls/sys_exit"
            ftrace_events: "sched/sched_process_exit"
            ftrace_events: "sched/sched_process_free"
            ftrace_events: "task/task_newtask"
            ftrace_events: "task/task_rename"
            ftrace_events: "ftrace/print"
            atrace_categories: "gfx"
            atrace_categories: "input"
            atrace_categories: "view"
            atrace_categories: "webview"
            atrace_categories: "wm"
            atrace_categories: "am"
            atrace_categories: "sm"
            atrace_categories: "audio"
            atrace_categories: "video"
            atrace_categories: "camera"
            atrace_categories: "hal"
            atrace_categories: "res"
            atrace_categories: "freq"
            atrace_categories: "dalvik"
            atrace_categories: "rs"
            atrace_categories: "bionic"
            atrace_categories: "power"
            atrace_categories: "pm"
            atrace_categories: "ss"
            atrace_categories: "database"
            atrace_categories: "network"
            atrace_categories: "adb"
            atrace_categories: "vibrator"
            atrace_categories: "aidl"
            atrace_categories: "nnapi"
            atrace_categories: "rro"
            atrace_categories: "binder_driver"
            atrace_categories: "binder_lock"
			atrace_apps: "com.example.memory.test"
        }
    }
}
duration_ms: 10000
write_into_file: true
file_write_period_ms: 2500
max_file_size_bytes: 10000000000
flush_period_ms: 30000
incremental_state_config {
    clear_period_ms: 5000
}



config_id.pbtx

...

buffers: {
    size_kb: 30720
    fill_policy: RING_BUFFER
}
buffers: {
    size_kb: 15360
    fill_policy: RING_BUFFER
}
data_sources: {
    config {
        name: "linux.process_stats"
        target_buffer: 1
        process_stats_config {
            scan_all_processes_on_start: true
        }
    }
}

data_sources: {
    config {
        name: "linux.sys_stats"
        sys_stats_config {
            meminfo_period_ms: 250
            meminfo_counters: MEMINFO_CMA_FREE
            meminfo_counters: MEMINFO_MEM_AVAILABLE
            meminfo_counters: MEMINFO_MEM_FREE
            stat_period_ms: 250
            stat_counters: STAT_CPU_TIMES
            stat_counters: STAT_FORK_COUNT
        }
    }
}

data_sources: {
    config {
        name: "linux.sys_stats"
        sys_stats_config {
            meminfo_period_ms: 1
            meminfo_counters: MEMINFO_ACTIVE_FILE
            meminfo_counters: MEMINFO_CACHED
            meminfo_counters: MEMINFO_INACTIVE_FILE
            meminfo_counters: MEMINFO_MEM_AVAILABLE
            meminfo_counters: MEMINFO_MEM_FREE
            vmstat_period_ms: 1
            vmstat_counters: VMSTAT_ALLOCSTALL
            vmstat_counters: VMSTAT_KSWAPD_HIGH_WMARK_HIT_QUICKLY
            vmstat_counters: VMSTAT_KSWAPD_LOW_WMARK_HIT_QUICKLY
            vmstat_counters: VMSTAT_WORKINGSET_REFAULT
        }
    }
}

data_sources: {
    config {
        name: "android.log"
        android_log_config {
            log_ids: LID_DEFAULT
            log_ids: LID_RADIO
            log_ids: LID_EVENTS
            log_ids: LID_SYSTEM
            log_ids: LID_CRASH
            log_ids: LID_STATS
            log_ids: LID_SECURITY
            log_ids: LID_KERNEL
        }
    }
}
data_sources: {
    config {
        name: "linux.ftrace"
        ftrace_config {
            ftrace_events: "sched/sched_switch"
            ftrace_events: "power/suspend_resume"
            ftrace_events: "sched/sched_wakeup"
            ftrace_events: "sched/sched_wakeup_new"
            ftrace_events: "sched/sched_waking"
			ftrace_events: "sched/sched_blocked_reason"
            ftrace_events: "power/cpu_frequency"
            ftrace_events: "power/cpu_idle"
            ftrace_events: "power/gpu_frequency"
            ftrace_events: "sched/sched_process_exit"
            ftrace_events: "sched/sched_process_free"
            ftrace_events: "task/task_newtask"
            ftrace_events: "task/task_rename"
            ftrace_events: "block/block_rq_insert"
            ftrace_events: "filemap/filemap_op_page_cache_miss"
            ftrace_events: "f2fs/f2fs_readpage"
            ftrace_events: "f2fs/f2fs_readpages"
            ftrace_events: "erofs/erofs_readpage"
            ftrace_events: "erofs/erofs_readpages"
            ftrace_events: "scsi/scsi_dispatch_cmd_start"
            ftrace_events: "scsi/scsi_dispatch_cmd_start_lifetime"
            ftrace_events: "scsi/scsi_dispatch_cmd_done"
            atrace_categories: "gfx"
            atrace_categories: "view"
            atrace_categories: "wm"
            atrace_categories: "am"
            atrace_categories: "pm"
            atrace_categories: "ss"
            atrace_categories: "power"
            atrace_categories: "binder_lock"
            atrace_categories: "freq"
            atrace_categories: "input"
            atrace_categories: "disk"
            atrace_categories: "idle"
            atrace_categories: "binder_driver"
            atrace_categories: "dalvik"
            atrace_categories: "pagecache"
            atrace_categories: "workq"
			atrace_apps: "com.example.memory.test"

        }
    }
}

duration_ms: 10000
write_into_file: true
file_write_period_ms: 2000
max_file_size_bytes: 1500000000
flush_period_ms: 3000

...


config_native_heap.pbtx

...
buffers: {
    size_kb: 634880
    fill_policy: DISCARD
}

data_sources: {
    config {
        name: "linux.process_stats"
        target_buffer: 0
        process_stats_config {
            scan_all_processes_on_start: true
            proc_stats_poll_ms: 100
        }
    }
}
data_sources: {
    config {
        name: "linux.sys_stats"
        sys_stats_config {
            vmstat_period_ms: 100
        }
    }
}
data_sources: {
    config {
        name: "android.heapprofd"
        target_buffer: 0
        heapprofd_config {
            sampling_interval_bytes: 4096
            continuous_dump_config {
                dump_phase_ms: 100
                dump_interval_ms: 100
            }
            shmem_size_bytes: 8388608
            block_client: true
			process_cmdline: "com.example.memory.test"
        }
    }
}


duration_ms: 10000
write_into_file: true
flush_period_ms:1000
data_sources {
  config {
    name: "android.packages_list"
  }
}


...


