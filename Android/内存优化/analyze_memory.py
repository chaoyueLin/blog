#!/usr/bin/env python3
"""匿名内存三步排查法 - 分析工具

用法: python analyze_memory.py <showmap_file> <smaps_file> <maps_file>

示例: python analyze_memory.py 管家.txt smaps.txt maps.txt
"""

import sys
import re
from collections import defaultdict
from pathlib import Path


def check_file(path, label):
    if not Path(path).exists():
        print(f'Error: {label} file not found: {path}')
        sys.exit(1)
    return path


def analyze_showmap(showmap_path, output_path):
    with open(showmap_path, 'r', encoding='utf-8', errors='replace') as f:
        lines = f.readlines()

    out = []
    out.append('=' * 90)
    out.append(f'Step 1: showmap 全景扫描 - 按名称聚合 RSS/PSS')
    out.append(f'输入: {showmap_path}')
    out.append('=' * 90)

    groups = defaultdict(lambda: {'rss': 0, 'pss': 0, 'virt': 0, 'count': 0})
    for line in lines[3:]:
        parts = line.split()
        if len(parts) < 17:
            continue
        try:
            rss = int(parts[1])
        except ValueError:
            continue
        pss = int(parts[2])
        virt = int(parts[0])
        name = ' '.join(parts[16:])
        groups[name]['rss'] += rss
        groups[name]['pss'] += pss
        groups[name]['virt'] += virt
        groups[name]['count'] += 1

    out.append(f'\nTop 30 by RSS:')
    hdr = f'{"RSS":>8} {"PSS":>8} {"VIRT":>9} {"Count":>5}  Name'
    out.append(hdr)
    out.append('-' * 90)
    for name, g in sorted(groups.items(), key=lambda x: x[1]['rss'], reverse=True)[:30]:
        out.append(f'{g["rss"]:8d} {g["pss"]:8d} {g["virt"]:9d} {g["count"]:5d}  {name}')

    out.append(f'\nTop 25 by PSS (实际内存代价):')
    out.append(hdr)
    out.append('-' * 90)
    for name, g in sorted(groups.items(), key=lambda x: x[1]['pss'], reverse=True)[:25]:
        out.append(f'{g["rss"]:8d} {g["pss"]:8d} {g["virt"]:9d} {g["count"]:5d}  {name}')

    with open(output_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(out))
    print(f'  -> {output_path}')


def analyze_smaps(smaps_path, output_path):
    with open(smaps_path, 'r', encoding='utf-8', errors='replace') as f:
        smap_lines = f.readlines()

    out = []
    out.append('=' * 90)
    out.append('Step 2: smaps 无名匿名区域定位 - 逐区域分析')
    out.append(f'输入: {smaps_path}')
    out.append('=' * 90)

    regions = []
    i = 0
    while i < len(smap_lines):
        line = smap_lines[i].rstrip()
        m = re.match(r'^([0-9a-f]+-[0-9a-f]+)\s+(\S+)\s+\S+\s+00:00\s+0\s*$', line)
        if m:
            addr = m.group(1)
            perms = m.group(2)
            rss = 0
            pss = 0
            for j in range(i + 1, min(i + 30, len(smap_lines))):
                sl = smap_lines[j]
                if sl.startswith('Rss:'):
                    rss = int(sl.split(':')[1].strip().split()[0])
                elif sl.startswith('Pss:'):
                    pss = int(sl.split(':')[1].strip().split()[0])
                elif sl.startswith('VmFlags:') or re.match(r'^[0-9a-f]+-', sl):
                    break
            start, end = addr.split('-')
            size_kb = (int(end, 16) - int(start, 16)) // 1024
            regions.append((size_kb, rss, pss, perms, addr))
            i += 1
        else:
            i += 1

    regions.sort(key=lambda x: x[1], reverse=True)

    total_rss = sum(rss for _, rss, _, _, _ in regions)
    total_virt = sum(sz for sz, _, _, _, _ in regions)
    total_pss = sum(pss for _, _, pss, _, _ in regions)

    out.append(f'\n无名匿名区域 (00:00 0, 无标签): {len(regions)} 个')
    out.append(f'总 VIRT: {total_virt} KB ({total_virt // 1024} MB)')
    out.append(f'总 RSS:  {total_rss} KB ({total_rss // 1024} MB)')
    out.append(f'总 PSS:  {total_pss} KB ({total_pss // 1024} MB)')
    out.append('')

    perms_groups = defaultdict(lambda: {'count': 0, 'rss': 0, 'pss': 0, 'virt': 0})
    for sz, rss, pss, perms, addr in regions:
        perms_groups[perms]['count'] += 1
        perms_groups[perms]['rss'] += rss
        perms_groups[perms]['pss'] += pss
        perms_groups[perms]['virt'] += sz

    out.append('按权限分类:')
    out.append(f'{"Perms":>6} {"Count":>5} {"RSS":>8} {"PSS":>8} {"VIRT":>9}')
    out.append('-' * 50)
    for perms, g in sorted(perms_groups.items(), key=lambda x: x[1]['rss'], reverse=True):
        out.append(f'{perms:>6} {g["count"]:5d} {g["rss"]:8d} {g["pss"]:8d} {g["virt"]:9d}')

    out.append(f'\nTop 30 无名匿名区域 by RSS:')
    out.append(f'{"RSS":>7} {"PSS":>7} {"VIRT":>7} {"Perms":>5}  Address')
    out.append('-' * 70)
    for sz, rss, pss, perms, addr in regions[:30]:
        out.append(f'{rss:7d} {pss:7d} {sz:7d} {perms:>5}  {addr}')

    with open(output_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(out))
    print(f'  -> {output_path}')


def analyze_maps(maps_path, output_path):
    with open(maps_path, 'r', encoding='utf-8', errors='replace') as f:
        map_lines = f.readlines()

    out = []
    out.append('=' * 90)
    out.append('Step 3: maps 邻居分析 - 无名 r--p 区域归属判定')
    out.append(f'输入: {maps_path}')
    out.append('=' * 90)

    anon_lines = []
    for line in map_lines:
        s = line.rstrip()
        if re.search(r'00:00\s+0\s*$', s):
            if not re.search(r'\[', s):
                anon_lines.append(s.strip())

    out.append(f'\n共 {len(anon_lines)} 个无标签匿名映射 (inode=0)')
    out.append('')

    rp_count = 0
    for i, line in enumerate(anon_lines):
        if 'r--p' in line and '[' not in line:
            addr = line.split()[0]
            s, e = addr.split('-')
            size_kb = (int(e, 16) - int(s, 16)) // 1024
            rp_count += 1
            out.append(f'--- #{rp_count} r--p {size_kb} KB ({size_kb // 1024} MB) ---')
            if i > 0:
                out.append(f'  BEFORE: {anon_lines[i - 1]}')
            if i < len(anon_lines) - 1:
                out.append(f'  AFTER:  {anon_lines[i + 1]}')
            out.append('')

    out.append(f'共 {rp_count} 个无名 r--p 区域')
    if rp_count > 0:
        out.append('\n结论: 检查 r--p 区域是否与 scudo/heap 等标签交替出现，判定归属')

    with open(output_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(out))
    print(f'  -> {output_path}')


def main():
    if len(sys.argv) < 4:
        print('用法: python analyze_memory.py <showmap_file> <smaps_file> <maps_file>')
        print('示例: python analyze_memory.py 管家.txt smaps.txt maps.txt')
        sys.exit(1)

    showmap_file = check_file(sys.argv[1], 'showmap')
    smaps_file = check_file(sys.argv[2], 'smaps')
    maps_file = check_file(sys.argv[3], 'maps')

    print('开始分析...')
    analyze_showmap(showmap_file, 'step1_showmap_analysis.txt')
    analyze_smaps(smaps_file, 'step2_smaps_analysis.txt')
    analyze_maps(maps_file, 'step3_maps_analysis.txt')
    print('分析完成。')


if __name__ == '__main__':
    main()
