#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
补充下载缺失的图片
根据实际需要下载所有缺失的图片
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

# 需要下载的图片列表
REQUIRED_IMAGES = {
    # 轮播图（如果不存在或太小则重新下载）
    "image1.png": {
        "url": "https://images.pexels.com/photos/1300975/pexels-photo-1300975.jpeg?auto=compress&cs=tinysrgb&w=1920&h=600&fit=crop",
        "fallback": "https://picsum.photos/1920/600?random=1",
        "min_size": 50000  # 最小50KB
    },
    "image2.png": {
        "url": "https://images.pexels.com/photos/1300970/pexels-photo-1300970.jpeg?auto=compress&cs=tinysrgb&w=1920&h=600&fit=crop",
        "fallback": "https://picsum.photos/1920/600?random=2",
        "min_size": 50000
    },
    "image3.png": {
        "url": "https://images.pexels.com/photos/1300971/pexels-photo-1300971.jpeg?auto=compress&cs=tinysrgb&w=1920&h=600&fit=crop",
        "fallback": "https://picsum.photos/1920/600?random=3",
        "min_size": 50000
    },
    # 用户头像
    "avatar.jpg": {
        "url": "https://images.pexels.com/photos/1300975/pexels-photo-1300975.jpeg?auto=compress&cs=tinysrgb&w=200&h=200&fit=crop",
        "fallback": "https://picsum.photos/200/200?random=100",
        "min_size": 5000
    },
}

def download_image(url, save_path, fallback_url=None, min_size=0):
    """下载图片并保存"""
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }
    
    try:
        print(f"  正在从 {url[:60]}... 下载...", end=" ")
        response = requests.get(url, headers=headers, proxies=PROXY, timeout=30, stream=True)
        response.raise_for_status()
        
        # 保存图片
        with open(save_path, 'wb') as f:
            for chunk in response.iter_content(chunk_size=8192):
                f.write(chunk)
        
        # 检查文件大小
        file_size = save_path.stat().st_size
        if min_size > 0 and file_size < min_size:
            print(f"✗ 文件太小 ({file_size} bytes < {min_size} bytes)")
            if fallback_url:
                print(f"  尝试备用URL...", end=" ")
                return download_image(fallback_url, save_path, None, min_size)
            return False
        
        print(f"✓ 成功 ({file_size // 1024}KB)")
        return True
        
    except requests.exceptions.RequestException as e:
        print(f"✗ 失败: {str(e)[:50]}")
        if fallback_url:
            print(f"  尝试备用URL...", end=" ")
            try:
                response = requests.get(fallback_url, headers=headers, proxies=PROXY, timeout=30, stream=True)
                response.raise_for_status()
                with open(save_path, 'wb') as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                file_size = save_path.stat().st_size
                if min_size > 0 and file_size < min_size:
                    print(f"✗ 备用URL文件也太小")
                    return False
                print(f"✓ 成功 ({file_size // 1024}KB)")
                return True
            except Exception as e2:
                print(f"✗ 备用URL也失败: {str(e2)[:50]}")
        return False
    except Exception as e:
        print(f"✗ 错误: {e}")
        return False

def main():
    """主函数"""
    print("\n" + "=" * 60)
    print("补充下载缺失的图片")
    print(f"使用代理: {PROXY['http']}")
    print(f"保存目录: {IMG_DIR}")
    print("=" * 60)
    
    downloaded = 0
    skipped = 0
    failed = 0
    
    for filename, config in REQUIRED_IMAGES.items():
        save_path = IMG_DIR / filename
        url = config["url"]
        fallback = config.get("fallback")
        min_size = config.get("min_size", 0)
        
        # 检查文件是否存在且有效
        need_download = True
        if save_path.exists():
            file_size = save_path.stat().st_size
            if file_size >= min_size:
                print(f"✓ {filename} - 文件已存在且有效 ({file_size // 1024}KB)，跳过")
                skipped += 1
                need_download = False
            else:
                print(f"⚠ {filename} - 文件已存在但太小 ({file_size} bytes < {min_size} bytes)，重新下载...")
                save_path.unlink()
        
        if need_download:
            print(f"下载: {filename}...")
            if download_image(url, save_path, fallback, min_size):
                downloaded += 1
            else:
                failed += 1
            
            # 避免请求过快
            time.sleep(0.5)
    
    print("\n" + "=" * 60)
    print("下载总结:")
    print(f"  成功下载: {downloaded}")
    print(f"  已存在跳过: {skipped}")
    print(f"  失败: {failed}")
    print("=" * 60)
    
    if failed == 0:
        print("\n✓ 所有图片检查完成!")
    else:
        print(f"\n⚠ 有 {failed} 个图片下载失败，请检查网络连接和代理设置")

if __name__ == "__main__":
    main()
