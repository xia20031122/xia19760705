#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
图片下载脚本 - 下载所有需要的图片
使用代理 127.0.0.1:7897
"""

import os
import requests
import time
from pathlib import Path

# 代理配置
PROXY = {
    'http': 'http://127.0.0.1:7897',
    'https': 'http://127.0.0.1:7897'
}

# 图片保存目录
IMG_DIR = Path(__file__).parent / "img"
IMG_DIR.mkdir(exist_ok=True)

# 轮播图图片URL（使用 Pexels 的免费图片，蔬菜水果主题）
BANNER_IMAGES = {
    "image1.png": "https://images.pexels.com/photos/1300975/pexels-photo-1300975.jpeg?auto=compress&cs=tinysrgb&w=1920&h=600&fit=crop",  # 新鲜蔬菜
    "image2.png": "https://images.pexels.com/photos/1300970/pexels-photo-1300970.jpeg?auto=compress&cs=tinysrgb&w=1920&h=600&fit=crop",  # 水果蔬菜
    "image3.png": "https://images.pexels.com/photos/1300971/pexels-photo-1300971.jpeg?auto=compress&cs=tinysrgb&w=1920&h=600&fit=crop",  # 有机蔬菜
}

# 分类图标图片（使用 Pexels 的免费图片）
CATEGORY_ICONS = {
    "category_leafy.png": "https://images.pexels.com/photos/1300975/pexels-photo-1300975.jpeg?auto=compress&cs=tinysrgb&w=200&h=200&fit=crop",  # 叶菜类
    "category_root.png": "https://images.pexels.com/photos/1300972/pexels-photo-1300972.jpeg?auto=compress&cs=tinysrgb&w=200&h=200&fit=crop",  # 根茎类
    "category_melon.png": "https://images.pexels.com/photos/1300970/pexels-photo-1300970.jpeg?auto=compress&cs=tinysrgb&w=200&h=200&fit=crop",  # 瓜果类
    "category_bean.png": "https://images.pexels.com/photos/1300974/pexels-photo-1300974.jpeg?auto=compress&cs=tinysrgb&w=200&h=200&fit=crop",  # 豆类
    "category_mushroom.png": "https://images.pexels.com/photos/1300973/pexels-photo-1300973.jpeg?auto=compress&cs=tinysrgb&w=200&h=200&fit=crop",  # 菌菇类
    "category_fruit.png": "https://images.pexels.com/photos/1300970/pexels-photo-1300970.jpeg?auto=compress&cs=tinysrgb&w=200&h=200&fit=crop",  # 水果类
}

# 备用图片URL（如果主URL失败）
FALLBACK_IMAGES = {
    "image1.png": "https://picsum.photos/1920/600?random=1",
    "image2.png": "https://picsum.photos/1920/600?random=2",
    "image3.png": "https://picsum.photos/1920/600?random=3",
}

def download_image(url, save_path, fallback_url=None):
    """下载图片并保存"""
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }
    
    try:
        print(f"  正在从 {url} 下载...", end=" ")
        response = requests.get(url, headers=headers, proxies=PROXY, timeout=30, stream=True)
        response.raise_for_status()
        
        # 保存图片
        with open(save_path, 'wb') as f:
            for chunk in response.iter_content(chunk_size=8192):
                f.write(chunk)
        
        print("✓ 成功")
        return True
        
    except requests.exceptions.RequestException as e:
        print(f"✗ 失败: {e}")
        if fallback_url:
            print(f"  尝试备用URL: {fallback_url}...", end=" ")
            try:
                response = requests.get(fallback_url, headers=headers, proxies=PROXY, timeout=30, stream=True)
                response.raise_for_status()
                with open(save_path, 'wb') as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                print("✓ 成功")
                return True
            except Exception as e2:
                print(f"✗ 备用URL也失败: {e2}")
        return False
    except Exception as e:
        print(f"✗ 错误: {e}")
        return False

def download_banner_images():
    """下载轮播图图片"""
    print("=" * 60)
    print("开始下载轮播图图片...")
    print(f"保存目录: {IMG_DIR}")
    print("-" * 60)
    
    downloaded = 0
    failed = 0
    
    for filename, url in BANNER_IMAGES.items():
        save_path = IMG_DIR / filename
        
        # 如果文件已存在，检查文件大小，如果太小则重新下载
        if save_path.exists():
            file_size = save_path.stat().st_size
            if file_size < 1000:  # 如果文件小于1KB，可能是无效图片
                print(f"⚠ {filename} - 文件已存在但可能无效（大小: {file_size} bytes），重新下载...")
                save_path.unlink()
            else:
                print(f"⚠ {filename} - 文件已存在，跳过")
                continue
        
        # 获取备用URL
        fallback_url = FALLBACK_IMAGES.get(filename)
        
        print(f"下载: {filename}...")
        if download_image(url, save_path, fallback_url):
            downloaded += 1
        else:
            failed += 1
        
        # 避免请求过快
        time.sleep(0.5)
    
    print("-" * 60)
    print(f"轮播图下载完成! 成功: {downloaded}, 失败: {failed}")
    return downloaded, failed

def download_category_icons():
    """下载分类图标"""
    print("\n" + "=" * 60)
    print("开始下载分类图标...")
    print(f"保存目录: {IMG_DIR}")
    print("-" * 60)
    
    downloaded = 0
    failed = 0
    
    for filename, url in CATEGORY_ICONS.items():
        save_path = IMG_DIR / filename
        
        # 如果文件已存在，跳过
        if save_path.exists():
            print(f"⚠ {filename} - 文件已存在，跳过")
            continue
        
        print(f"下载: {filename}...")
        if download_image(url, save_path):
            downloaded += 1
        else:
            failed += 1
        
        # 避免请求过快
        time.sleep(0.5)
    
    print("-" * 60)
    print(f"分类图标下载完成! 成功: {downloaded}, 失败: {failed}")
    return downloaded, failed

def main():
    """主函数"""
    print("\n" + "=" * 60)
    print("图片下载脚本")
    print(f"使用代理: {PROXY['http']}")
    print("=" * 60)
    
    # 下载轮播图
    banner_success, banner_failed = download_banner_images()
    
    # 下载分类图标
    icon_success, icon_failed = download_category_icons()
    
    # 总结
    print("\n" + "=" * 60)
    print("下载总结:")
    print(f"  轮播图: 成功 {banner_success}, 失败 {banner_failed}")
    print(f"  分类图标: 成功 {icon_success}, 失败 {icon_failed}")
    print(f"  总计: 成功 {banner_success + icon_success}, 失败 {banner_failed + icon_failed}")
    print("=" * 60)
    
    if banner_failed + icon_failed == 0:
        print("\n✓ 所有图片下载成功!")
    else:
        print(f"\n⚠ 有 {banner_failed + icon_failed} 个图片下载失败，请检查网络连接和代理设置")

if __name__ == "__main__":
    main()
