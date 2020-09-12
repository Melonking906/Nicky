package io.loyloy.nicky;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.loyloy.nicky.databases.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * A nickname query.
 */
public class NickQuery {
	/**
	 * A queried nickname.
	 */
	static public final class Result {
		private final OfflinePlayer player;
		private final String nickname;
		private final String userName;
		private final UUID userId;

		Result( OfflinePlayer player, String nickname, String userName, UUID userId ) {
			this.player = player;
			this.nickname = nickname;
			this.userName = userName;
			this.userId = userId;
		}

		/**
		 * Gets the nickname with color codes.
		 *
		 * @return The nickname.
		 */
		public String getNickname()
		{
			return nickname;
		}

		/**
		 * Gets the plain nickname without color codes.
		 *
		 * @return The plain nickname.
		 */
		public String getPlainNickname()
		{
			return ChatColor.stripColor(nickname);
		}

		/**
		 * Gets the OfflinePlayer associated with this nickname.
		 * @return The offline player.
		 */
		public OfflinePlayer getPlayer()
		{
			return this.player;
		}

		/**
		 * Gets the username of the player with this nickname.
		 * @return The player's username.
		 */
		public String getUsername()
		{
			String offlineUsername = this.getPlayer().getName();
			return offlineUsername == null ? this.userName : offlineUsername;
		}

		/**
		 * Gets the UUID of the player with this nickname.
		 * @return The player's UUID.
		 */
		public UUID getUUID()
		{
			return this.userId;
		}

		@Override
		public boolean equals( Object o ) 
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Result result = (Result) o;
			return Objects.equals(nickname, result.nickname) &&
					Objects.equals(userName, result.userName) &&
					Objects.equals(userId, result.userId);
		}

		@Override
		public int hashCode() 
		{
			return Objects.hash(nickname, userName, userId);
		}
	}
	
	/**
	 * Searches for players with a nickname.
	 *
	 * @param query The nickname.
	 * @return A future task that searches for matching players.
	 */
	@SuppressWarnings("deprecation")
	static public CompletableFuture<Set<Result>> find(String query) {
		return CompletableFuture.supplyAsync( () -> {
			Set<Result> results = new HashSet<>();
			List<SQL.SearchedPlayer> searchedPlayers = Nick.searchGet(query);

			if (searchedPlayers != null) {
				for (SQL.SearchedPlayer searchedPlayer : searchedPlayers) {
					UUID uuid = UUID.fromString(searchedPlayer.getUuid());
					String nick = Nick.formatForServer(searchedPlayer.getNick());
					OfflinePlayer player = Nicky.plugin.getServer().getOfflinePlayer( uuid );

					results.add(new Result(
							player,
							nick,
							searchedPlayer.getName(),
							uuid
					));
				}
			}

			return results;
		} );
	}


}
