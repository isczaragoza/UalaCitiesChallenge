package com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.infrastructure.R
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.Grey600
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.Grey800
import com.isczaragoza.ualacitieschallenge.infrastructure.designsystem.theme.UalaCitiesChallengeTheme

@Composable
fun CityItemCard(city: City, onItemClick: (City) -> Unit, onFavClick: (City) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 2.5.dp)
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable {
                    onItemClick(city)
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, top = 15.dp, end = 10.dp),
                        text = "${city.name} - ${city.country}",
                        fontSize = 26.sp,
                        color = Grey800,
                        maxLines = 3,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, top = 20.dp, end = 10.dp),
                        text = stringResource(
                            R.string.city_card_item_location,
                            city.lat.toString(),
                            city.lon.toString()
                        ),
                        fontSize = 16.sp,
                        color = Grey600,
                        maxLines = 2,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = {
                        onFavClick(city)
                    },
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Icon(
                        imageVector = if (city.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable()
private fun CityItemCardPreview() {
    UalaCitiesChallengeTheme {
        CityItemCard(
            City(
                id = 100L,
                name = "Huehuetoca",
                country = "México",
                isFavorite = false,
                lat = 2.230,
                lon = -3.402,
            ),
            {},
            {}
        )
    }
}