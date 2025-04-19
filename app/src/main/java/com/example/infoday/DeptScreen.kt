package com.example.infoday

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
//import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.infoday.ui.theme.InfoDayTheme
import androidx.compose.material3.HorizontalDivider

data class Dept(val name: String, val id: String) {
    companion object {
        val data = listOf(
            Dept("Computer Science", "COMP"),
            Dept("Communication Studies", "COMS")
        )
    }
}

@Composable
fun DeptScreen(navController: NavHostController) {
    LazyColumn {
        items(Dept.data) { dept ->
            ListItem(
                headlineContent = { Text(dept.name) },
                modifier = Modifier.clickable {
                    navController.navigate("event/${dept.id}")
                },
                leadingContent = {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = null
                    )
                }
            )
            HorizontalDivider()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DeptPreview() {
    InfoDayTheme {
        DeptScreen(rememberNavController())
    }
}