package com.example.Assignment3

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    // 保存用户令牌、ID和Email
    fun saveUserInfo(token: String, id: String, email: String) {
        sharedPreferences.edit()
            .putString("USER_TOKEN", token)
            .putString("USER_ID", id)
            .putString("USER_EMAIL", email)
            .apply()
    }

    // 获取用户令牌
    fun getToken(): String? {
        return sharedPreferences.getString("USER_TOKEN", null)
    }

    // 获取用户ID
    fun getId(): String? {
        return sharedPreferences.getString("USER_ID", null)
    }

    // 获取用户Email
    fun getEmail(): String? {
        return sharedPreferences.getString("USER_EMAIL", null)
    }

    // 登出
    fun logout() {
        sharedPreferences.edit()
            .remove("USER_TOKEN")
            .remove("USER_ID")
            .remove("USER_EMAIL")
            .apply()
    }

    // 添加预约设备 - 基于用户Email
    fun addReservedEquipment(equipmentId: String) {
        val email = getEmail() ?: return
        val key = "RESERVED_${email}"
        val reservedSet = getReservedEquipmentIds().toMutableSet()
        reservedSet.add(equipmentId)
        sharedPreferences.edit()
            .putStringSet(key, reservedSet)
            .apply()
    }

    // 移除预约设备 - 基于用户Email
    fun removeReservedEquipment(equipmentId: String) {
        val email = getEmail() ?: return
        val key = "RESERVED_${email}"
        val reservedSet = getReservedEquipmentIds().toMutableSet()
        reservedSet.remove(equipmentId)
        sharedPreferences.edit()
            .putStringSet(key, reservedSet)
            .apply()
    }

    // 获取所有预约设备ID - 基于用户Email
    fun getReservedEquipmentIds(): Set<String> {
        val email = getEmail() ?: return emptySet()
        val key = "RESERVED_${email}"
        return sharedPreferences.getStringSet(key, emptySet()) ?: emptySet()
    }

    // 检查设备是否已预约 - 基于用户Email
    fun isEquipmentReserved(equipmentId: String): Boolean {
        return getReservedEquipmentIds().contains(equipmentId)
    }
}