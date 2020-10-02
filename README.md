Nicky
=====
Simple SQL Nickname Plugin for Bukkit, Spigot, and Paper.


## Commands

<details>
<summary><b>/nick &lt;name&gt;</b></summary>
<div>
<b>Description:</b> Sets your nickname.<br/>
<b>Permission:</b> <code>nicky.set</code>
</div>
</details>

<details>
<summary><b>/nick &lt;player&gt; &lt;name&gt;</b></summary>
<div>
<b>Description:</b> Sets another player's nickname.<br/>
<b>Permission:</b> <code>nicky.set.other</code>
</div>
</details>

<details>
<summary><b>/delnick</b></summary>
<div>
<b>Description:</b> Removes your nickname.<br/>
<b>Permission:</b> <code>nicky.del</code>
</div>
</details>

<details>
<summary><b>/delnick &lt;player&gt;</b></summary>
<div>
<b>Description:</b> Removes another player's nickname.<br/>
<b>Permission:</b> <code>nicky.del.other</code> or <code>nicky.set.other</code>
</div>
</details>

<details>
<summary><b>/realname &lt;nickname&gt;</b></summary>
<div>
<b>Description:</b> Searches for any players with a matching nickname.<br/>
<b>Permission:</b> <code>nicky.realname</code>
</div>
</details>


## Permissions

|Permission|Description|
|:--|:--|
|`nicky.set`|Access to the `/nick` command.|
|`nicky.set.other`|Permission to change other players' nicknames.|
|`nicky.del`|Access to the `/delnick` command.|
|`nicky.del.other`|Permission to remove other players' nicknames.|
|`nicky.color`|Permission to use all colors and formatting in nicknames.|
|`nicky.color.normal`|Permission to use all colors in nicknames.|
|`nicky.color.extra`|Permission to use all formatting in nicknames.|
|`nicky.color.[code]`|Permission to use a specific color code in the player's nickname.|
|`nicky.limit.color.[n]`|If enabled, permission to use up to `n` unique colors in the player's nickname.|
|`nicky.noblacklist`|Bypass the nickname blacklist.|
|`nicky.reload`|Reload the Nicky config.|
|`nicky.help`|View the Nicky help (default).| 


## PlaceholderAPI
Nicky supports [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).

```
%nicky_nickname%
```
