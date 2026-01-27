#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
轮播图图片下载脚本
从网络下载蔬果相关的轮播图图片
"""

import os
import requests
import time
from pathlib import Path

# 图片保存目录
IMG_DIR = Path(__file__).parent / "img"
IMG_DIR.mkdir(exist_ok=True)

# 轮播图图片URL（使用公开的免费图片资源）
BANNER_IMAGES = {
    "image1.png": "https://images.pexels.com/photos/1300975/pexels-photo-1300975.jpeg?auto=compress&cs=tinysrgb&w=1920",
    "image2.png": "https://images.pexels.com/photos/1300970/pexels-photo-1300970.jpeg?auto=compress&cs=tinysrgb&w=1920",
    "image3.png": "https://images.pexels.com/photos/1300971/pexels-photo-1300971.jpeg?auto=compress&cs=tinysrgb&w=1920",
}

# 备用图片URL（如果主URL失败）
FALLBACK_IMAGES = {
    "image1.png": "https://picsum.photos/1920/600?random=1",
    "image2.png": "https://picsum.photos/1920/600?random=2",
    "image3.png": "https://picsum.photos/1920/600?random=3",
}

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

def download_banner_images():
    """下载所有轮播图图片"""
    print("开始下载轮播图图片...")
    print(f"保存目录: {IMG_DIR}")
    print("-" * 50)
    
    downloaded = 0
    failed = 0
    
    for filename, url in BANNER_IMAGES.items():
        save_path = IMG_DIR / filename
        
        # 如果文件已存在，跳过
        if save_path.exists():
            print(f"✓ {filename} - 文件已存在")
            continue
        
        # 获取备用URL
        fallback_url = FALLBACK_IMAGES.get(filename)
        
        print(f"正在下载: {filename}...", end=" ")
        if download_image(url, save_path, fallback_url):
            downloaded += 1
            print(f"✓ 成功")
        else:
            failed += 1
            print(f"✗ 失败")
        
        # 避免请求过快
        time.sleep(0.3)
    
    print("-" * 50)
    print(f"下载完成! 成功: {downloaded}, 失败: {failed}")

if __name__ == "__main__":
    download_banner_images()
