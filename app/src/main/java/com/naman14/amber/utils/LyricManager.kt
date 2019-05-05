package com.naman14.amber.utils

import android.util.LruCache


/**
 *  Created by huangxiaoyu on 2019/5/6
 *
 */

object LyricManager {

    val lrcCache = LruCache<String, String>(10)

}