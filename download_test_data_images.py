#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
下载测试数据中需要的所有图片
包括：轮播图（image1-3.png）和蔬菜图片（vegetable001-012.jpg）
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

# 图片保存目录（项目根目录下的img文件夹）
IMG_DIR = Path(__file__).parent / "img"
IMG_DIR.mkdir(exist_ok=True)

# 蔬菜图片映射（从Pexels下载对应的蔬菜图片）
VEGETABLE_IMAGES = {
    "vegetable001.jpg": {  # 新鲜小白菜
        "url": "https://images.pexels.com/photos/1300975/pexels-photo-1300975.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=101"
    },
    "vegetable002.jpg": {  # 有机胡萝卜
        "url": "https://images.pexels.com/photos/1300972/pexels-photo-1300972.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=102"
    },
    "vegetable003.jpg": {  # 精品西红柿
        "url": "https://images.pexels.com/photos/1300970/pexels-photo-1300970.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=103"
    },
    "vegetable004.jpg": {  # 新鲜黄瓜
        "url": "https://images.pexels.com/photos/1300970/pexels-photo-1300970.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=104"
    },
    "vegetable005.jpg": {  # 优质土豆
        "url": "https://images.pexels.com/photos/1300972/pexels-photo-1300972.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=105"
    },
    "vegetable006.jpg": {  # 新鲜菠菜
        "url": "https://images.pexels.com/photos/1300975/pexels-photo-1300975.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=106"
    },
    "vegetable007.jpg": {  # 精品豆角
        "url": "https://images.pexels.com/photos/1300974/pexels-photo-1300974.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=107"
    },
    "vegetable008.jpg": {  # 新鲜香菇
        "url": "https://images.pexels.com/photos/1300973/pexels-photo-1300973.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=108"
    },
    "vegetable009.jpg": {  # 新鲜苹果
        "url": "https://images.pexels.com/photos/1300970/pexels-photo-1300970.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=109"
    },
    "vegetable010.jpg": {  # 精品大葱
        "url": "https://images.pexels.com/photos/1300975/pexels-photo-1300975.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=110"
    },
    "vegetable011.jpg": {  # 新鲜韭菜
        "url": "https://images.pexels.com/photos/1300975/pexels-photo-1300975.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=111"
    },
    "vegetable012.jpg": {  # 优质茄子
        "url": "https://images.pexels.com/photos/1300970/pexels-photo-1300970.jpeg?auto=compress&cs=tinysrgb&w=800&h=600&fit=crop",
        "fallback": "https://picsum.photos/800/600?random=112"
    },
}

# 轮播图
BANNER_IMAGES = {
    "image1.png": {
        "url": "https://images.pexels.com/photos/1300975/pexels-photo-1300975.jpeg?auto=compress&cs=tinysrgb&w=1920&h=600&fit=crop",
        "fallback": "https://picsum.photos/1920/600?random=1"
    },
    "image2.png": {
        "url": "https://images.pexels.com/photos/1300970/pexels-photo-1300970.jpeg?auto=compress&cs=tinysrgb&w=1920&h=600&fit=crop",
        "fallback": "https://picsum.photos/1920/600?random=2"
    },
    "image3.png": {
        "url": "https://images.pexels.com/photos/1300971/pexels-photo-1300971.jpeg?auto=compress&cs=tinysrgb&w=1920&h=600&fit=crop",
        "fallback": "https://picsum.photos/1920/600?random=3"
    },
}

def download_image(url, save_path, fallback_url=None, min_size=1000):
    """下载图片并保存"""
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }
    
    # 检查文件是否已存在且大小合理
    if save_path.exists():
        file_size = save_path.stat().st_size
        if file_size >= min_size:
            print(f"✓ {save_path.name} 已存在 ({file_size} bytes)，跳过下载")
            return True
    
    try:
        print(f"正在下载: {save_path.name}...")
        response = requests.get(url, headers=headers, proxies=PROXY, timeout=30, stream=True)
        
        if response.status_code == 200:
            # 使用流式下载
            total_size = 0
            with open(save_path, 'wb') as f:
                for chunk in response.iter_content(chunk_size=8192):
                    if chunk:
                        f.write(chunk)
                        total_size += len(chunk)
            
            # 检查下载的文件大小
            if total_size >= min_size:
                print(f"✓ 下载成功: {save_path.name} ({total_size} bytes)")
                return True
            else:
                print(f"✗ 下载的文件太小: {save_path.name} ({total_size} bytes < {min_size} bytes)")
                save_path.unlink()  # 删除无效文件
                if fallback_url:
                    print(f"  尝试备用URL: {fallback_url}")
                    return download_image(fallback_url, save_path, None, min_size)
                return False
        else:
            print(f"✗ 下载失败: {save_path.name} (状态码: {response.status_code})")
            if fallback_url:
                print(f"  尝试备用URL: {fallback_url}")
                return download_image(fallback_url, save_path, None, min_size)
            return False
            
    except Exception as e:
        print(f"✗ 下载出错: {save_path.name} - {str(e)}")
        if fallback_url:
            print(f"  尝试备用URL: {fallback_url}")
            try:
                return download_image(fallback_url, save_path, None, min_size)
            except:
                pass
        return False

def main():
    """主函数"""
    print("=" * 60)
    print("开始下载测试数据所需的图片...")
    print(f"图片保存目录: {IMG_DIR}")
    print("=" * 60)
    
    # 合并所有需要下载的图片
    all_images = {**BANNER_IMAGES, **VEGETABLE_IMAGES}
    
    success_count = 0
    fail_count = 0
    
    for filename, image_info in all_images.items():
        save_path = IMG_DIR / filename
        url = image_info["url"]
        fallback = image_info.get("fallback")
        
        # 根据文件类型设置最小大小
        min_size = 50000 if filename.endswith('.png') else 10000  # PNG轮播图50KB，JPG蔬菜图10KB
        
        if download_image(url, save_path, fallback, min_size):
            success_count += 1
        else:
            fail_count += 1
        
        # 避免请求过快
        time.sleep(0.5)
    
    print("=" * 60)
    print(f"下载完成！成功: {success_count}, 失败: {fail_count}")
    print("=" * 60)
    
    # 列出所有已下载的文件
    if IMG_DIR.exists():
        files = list(IMG_DIR.glob("*"))
        if files:
            print(f"\n已下载的文件列表 ({len(files)} 个):")
            for f in sorted(files):
                size = f.stat().st_size
                print(f"  - {f.name} ({size:,} bytes)")

if __name__ == "__main__":
    main()
