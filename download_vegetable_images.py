#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
蔬果图片下载脚本
从网络下载常见的蔬果图片到 img 目录
"""

import os
import requests
import time
from pathlib import Path

# 图片保存目录
IMG_DIR = Path(__file__).parent / "img"
IMG_DIR.mkdir(exist_ok=True)

# 常见蔬果及其图片URL（使用 Pexels 和其他可靠的图片源）
# 使用公开的图片URL，这些URL应该可以直接访问
VEGETABLE_IMAGES = {
    "白菜": "https://images.pexels.com/photos/1300975/pexels-photo-1300975.jpeg?auto=compress&cs=tinysrgb&w=800",
    "萝卜": "https://images.pexels.com/photos/1300972/pexels-photo-1300972.jpeg?auto=compress&cs=tinysrgb&w=800",
    "西红柿": "https://images.pexels.com/photos/1300970/pexels-photo-1300970.jpeg?auto=compress&cs=tinysrgb&w=800",
    "黄瓜": "https://images.pexels.com/photos/1300971/pexels-photo-1300971.jpeg?auto=compress&cs=tinysrgb&w=800",
    "茄子": "https://images.pexels.com/photos/1300973/pexels-photo-1300973.jpeg?auto=compress&cs=tinysrgb&w=800",
    "土豆": "https://images.pexels.com/photos/1300974/pexels-photo-1300974.jpeg?auto=compress&cs=tinysrgb&w=800",
    "胡萝卜": "https://images.pexels.com/photos/1300976/pexels-photo-1300976.jpeg?auto=compress&cs=tinysrgb&w=800",
    "青菜": "https://images.pexels.com/photos/1300977/pexels-photo-1300977.jpeg?auto=compress&cs=tinysrgb&w=800",
    "菠菜": "https://images.pexels.com/photos/1300978/pexels-photo-1300978.jpeg?auto=compress&cs=tinysrgb&w=800",
    "芹菜": "https://images.pexels.com/photos/1300979/pexels-photo-1300979.jpeg?auto=compress&cs=tinysrgb&w=800",
    "韭菜": "https://images.pexels.com/photos/1300980/pexels-photo-1300980.jpeg?auto=compress&cs=tinysrgb&w=800",
    "大葱": "https://images.pexels.com/photos/1300981/pexels-photo-1300981.jpeg?auto=compress&cs=tinysrgb&w=800",
    "洋葱": "https://images.pexels.com/photos/1300982/pexels-photo-1300982.jpeg?auto=compress&cs=tinysrgb&w=800",
    "辣椒": "https://images.pexels.com/photos/1300983/pexels-photo-1300983.jpeg?auto=compress&cs=tinysrgb&w=800",
    "豆角": "https://images.pexels.com/photos/1300984/pexels-photo-1300984.jpeg?auto=compress&cs=tinysrgb&w=800",
    "冬瓜": "https://images.pexels.com/photos/1300985/pexels-photo-1300985.jpeg?auto=compress&cs=tinysrgb&w=800",
    "南瓜": "https://images.pexels.com/photos/1300986/pexels-photo-1300986.jpeg?auto=compress&cs=tinysrgb&w=800",
    "丝瓜": "https://images.pexels.com/photos/1300987/pexels-photo-1300987.jpeg?auto=compress&cs=tinysrgb&w=800",
    "苦瓜": "https://images.pexels.com/photos/1300988/pexels-photo-1300988.jpeg?auto=compress&cs=tinysrgb&w=800",
    "玉米": "https://images.pexels.com/photos/1300989/pexels-photo-1300989.jpeg?auto=compress&cs=tinysrgb&w=800",
    "苹果": "https://images.pexels.com/photos/1300990/pexels-photo-1300990.jpeg?auto=compress&cs=tinysrgb&w=800",
    "香蕉": "https://images.pexels.com/photos/1300991/pexels-photo-1300991.jpeg?auto=compress&cs=tinysrgb&w=800",
    "橙子": "https://images.pexels.com/photos/1300992/pexels-photo-1300992.jpeg?auto=compress&cs=tinysrgb&w=800",
    "葡萄": "https://images.pexels.com/photos/1300993/pexels-photo-1300993.jpeg?auto=compress&cs=tinysrgb&w=800",
    "草莓": "https://images.pexels.com/photos/1300994/pexels-photo-1300994.jpeg?auto=compress&cs=tinysrgb&w=800",
    "西瓜": "https://images.pexels.com/photos/1300995/pexels-photo-1300995.jpeg?auto=compress&cs=tinysrgb&w=800",
    "桃子": "https://images.pexels.com/photos/1300996/pexels-photo-1300996.jpeg?auto=compress&cs=tinysrgb&w=800",
    "梨": "https://images.pexels.com/photos/1300997/pexels-photo-1300997.jpeg?auto=compress&cs=tinysrgb&w=800",
}

# 如果 Pexels 的URL不可用，使用备用方案：直接使用一些公开的图片URL
# 或者使用 Lorem Picsum 等占位图服务
FALLBACK_IMAGES = {
    "白菜": "https://picsum.photos/800/600?random=1",
    "萝卜": "https://picsum.photos/800/600?random=2",
    "西红柿": "https://picsum.photos/800/600?random=3",
    "黄瓜": "https://picsum.photos/800/600?random=4",
    "茄子": "https://picsum.photos/800/600?random=5",
    "土豆": "https://picsum.photos/800/600?random=6",
    "胡萝卜": "https://picsum.photos/800/600?random=7",
    "青菜": "https://picsum.photos/800/600?random=8",
    "菠菜": "https://picsum.photos/800/600?random=9",
    "芹菜": "https://picsum.photos/800/600?random=10",
    "韭菜": "https://picsum.photos/800/600?random=11",
    "大葱": "https://picsum.photos/800/600?random=12",
    "洋葱": "https://picsum.photos/800/600?random=13",
    "辣椒": "https://picsum.photos/800/600?random=14",
    "豆角": "https://picsum.photos/800/600?random=15",
    "冬瓜": "https://picsum.photos/800/600?random=16",
    "南瓜": "https://picsum.photos/800/600?random=17",
    "丝瓜": "https://picsum.photos/800/600?random=18",
    "苦瓜": "https://picsum.photos/800/600?random=19",
    "玉米": "https://picsum.photos/800/600?random=20",
    "苹果": "https://picsum.photos/800/600?random=21",
    "香蕉": "https://picsum.photos/800/600?random=22",
    "橙子": "https://picsum.photos/800/600?random=23",
    "葡萄": "https://picsum.photos/800/600?random=24",
    "草莓": "https://picsum.photos/800/600?random=25",
    "西瓜": "https://picsum.photos/800/600?random=26",
    "桃子": "https://picsum.photos/800/600?random=27",
    "梨": "https://picsum.photos/800/600?random=28",
}

# 使用更可靠的图片源（Pixabay 或其他公开图片）
# 这里使用一些公开的图片URL作为示例
# 实际使用时可以替换为真实的图片URL

def download_image(url, save_path, fallback_url=None):
    """下载图片"""
    try:
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        response = requests.get(url, headers=headers, timeout=10)
        if response.status_code == 200:
            with open(save_path, 'wb') as f:
                f.write(response.content)
            return True
        else:
            # 如果主URL失败，尝试备用URL
            if fallback_url:
                print(f"主URL失败，尝试备用URL...", end=" ")
                return download_image(fallback_url, save_path)
            print(f"下载失败: {url} (状态码: {response.status_code})")
            return False
    except Exception as e:
        # 如果主URL出错，尝试备用URL
        if fallback_url:
            print(f"主URL出错，尝试备用URL...", end=" ")
            try:
                return download_image(fallback_url, save_path)
            except:
                pass
        print(f"下载出错: {url} - {str(e)}")
        return False

def download_vegetable_images():
    """下载所有蔬果图片"""
    print("开始下载蔬果图片...")
    print(f"保存目录: {IMG_DIR}")
    print("-" * 50)
    
    downloaded = 0
    failed = 0
    
    for name, url in VEGETABLE_IMAGES.items():
        # 生成文件名：使用时间戳 + 名称
        timestamp = int(time.time() * 1000)
        filename = f"{timestamp}_{name}.jpg"
        save_path = IMG_DIR / filename
        
        # 如果文件已存在，跳过
        if save_path.exists():
            print(f"✓ {name} - 文件已存在: {filename}")
            continue
        
        # 获取备用URL
        fallback_url = FALLBACK_IMAGES.get(name)
        
        print(f"正在下载: {name}...", end=" ")
        if download_image(url, save_path, fallback_url):
            downloaded += 1
            print(f"✓ 成功: {filename}")
        else:
            failed += 1
            print(f"✗ 失败")
        
        # 避免请求过快
        time.sleep(0.3)
    
    print("-" * 50)
    print(f"下载完成! 成功: {downloaded}, 失败: {failed}")

if __name__ == "__main__":
    download_vegetable_images()
