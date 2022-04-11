package com.kinsin.debug_udp_cat.util

import android.content.Context
import com.kinsin.debug_udp_cat.entity.BdInfo
import org.json.JSONArray
import org.json.JSONObject

/**
 * 存储广播信息的工具类
 * 并非使用数据库，只是使用共享参数，使用Tool来达到类似数据库的持久化操作
 * 主键就是action
 * 格式选用json
 */
class BDDatabaseTool {
    companion object {
        const val DATABASE_NAME = "SAVE_BD_INFO"

        var shareTool: ShareTool? = null

        fun getAll(context: Context): ArrayList<BdInfo> {
            val bdInfoList: ArrayList<BdInfo> = ArrayList()
            if (shareTool == null) {
                shareTool = ShareTool(context)
            }
            val result = shareTool!!.getShareString(DATABASE_NAME, "")
            if (result == "") {
                return bdInfoList
            } else {
                val json = JSONObject(result)
                val jsonArray = json.getJSONArray("data")
                for (i in 0 until jsonArray.length()) {
                    val bdInfoJson = jsonArray.getJSONObject(i)
                    val bdInfo = BdInfo()
                    bdInfo.action = bdInfoJson.optString("action")
                    val filterArray = bdInfoJson.getJSONArray("filters")
                    for (j in 0 until filterArray.length()) {
                        bdInfo.filterList.add(filterArray.get(j) as String)
                    }
                    bdInfoList.add(bdInfo)
                }
                return bdInfoList
            }
        }

        fun getInfoByAction(context: Context, action: String): BdInfo? {
            val bdInfoList = getAll(context)
            if (bdInfoList.isNotEmpty()) {
                for (bdInfo in bdInfoList) {
                    if (bdInfo.action == action) {
                        return bdInfo
                    }
                }
            }
            return null
        }

        fun insertOrUpdate(context: Context, bdInfo: BdInfo) {
            // 先判断是否存在，存在就直接让update
            val result = shareTool!!.getShareString(DATABASE_NAME, "")
            var json: JSONObject? = null
            var jsonArray = JSONArray()
            if (isExit(context, bdInfo.action)) {
                json = JSONObject(result)
                jsonArray = json.getJSONArray("data")
                update(context, bdInfo, jsonArray)
            } else{
                if (result == "") {
                    // 第一个, 不用做任何处理
                } else{
                    // 不是第一个
                    json = JSONObject(result)
                    jsonArray = json!!.getJSONArray("data")
                }
                val bdInfoJson = JSONObject()
                val filterJsonArray = JSONArray()
                bdInfoJson.put("action", bdInfo.action)
                for (filter in bdInfo.filterList) {
                    filterJsonArray.put(filter)
                }
                bdInfoJson.put("filters", filterJsonArray)
                jsonArray.put(bdInfoJson)
                json = JSONObject()
                json.put("data", jsonArray)
                shareTool!!.setShareString(DATABASE_NAME, json.toString())
            }
        }

        private fun update(context: Context, bdInfo: BdInfo, jsonArray: JSONArray) {
            val newJsonArray = JSONArray()
            // 将不匹配的info全部移动到新的jsonArray中去
            for (i in 0 until jsonArray.length()) {
                if (jsonArray.getJSONObject(i).optString("action") != bdInfo.action) {
                    newJsonArray.put(jsonArray.getJSONObject(i))
                }
            }
            // 将更新的info添加到新的jsonArray中去
            val bdInfoJson = JSONObject()
            val bdInfoJsonArray = JSONArray()
            bdInfoJson.put("action", bdInfo.action)
            for (filter in bdInfo.filterList) {
                bdInfoJsonArray.put(filter)
            }
            bdInfoJson.put("filters", bdInfoJsonArray)
            newJsonArray.put(bdInfoJson)
            val json = JSONObject()
            json.put("data", newJsonArray)
            shareTool!!.setShareString(DATABASE_NAME, json.toString())
        }

        fun delete(context: Context, action: String): Boolean {
            // 将不匹配的info全部移动到新的jsonArray中去保存即可
            val result = shareTool!!.getShareString(DATABASE_NAME, "")
            if (result == "") {
                return false
            }
            var json = JSONObject(result)
            val jsonArray = json.getJSONArray("data")
            val newJsonArray = JSONArray()
            for (i in 0 until jsonArray.length()) {
                if (jsonArray.getJSONObject(i).optString("action") != action) {
                    newJsonArray.put(jsonArray.getJSONObject(i))
                }
            }
            json = JSONObject()
            json.put("data", newJsonArray)
            shareTool!!.setShareString(DATABASE_NAME, json.toString())
            return true
        }

        fun deleteAll() {
            shareTool!!.setShareString(DATABASE_NAME, "")
        }

        private fun isExit(context: Context, action: String): Boolean {
            var bdInfoList = getAll(context)
            if (bdInfoList.isNotEmpty()) {
                for (bdInfo in bdInfoList) {
                    if (bdInfo.action == action) {
                        return true
                    }
                }
                return false
            } else{
                return false
            }
        }

    }

}