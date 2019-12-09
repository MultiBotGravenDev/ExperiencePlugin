package fr.gravendev.experience;

import fr.gravendev.DatabasePlugin;
import fr.neutronstars.nbot.api.NBot;
import fr.neutronstars.nbot.api.entity.NBotCustomUser;
import fr.neutronstars.nbot.api.entity.NBotUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ExperienceMember extends NBotCustomUser {

    /*
    CREATE TABLE IF NOT EXISTS `experience` (
            `id` int(11) NOT NULL AUTO_INCREMENT,
            `discord_id` char(50) NOT NULL,
             `experience` int(11) NOT NULL DEFAULT 0,
            `level` int(11) NOT NULL DEFAULT 0,
            `messages_count` int(11) NOT NULL DEFAULT 0,
            `last_message` datetime DEFAULT NULL,
            PRIMARY KEY (`id`),
            UNIQUE KEY `discord_id` (`discord_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=113718 DEFAULT CHARSET=utf8mb4;
    */

    private int experiences = 0, levels = 0, messages = 0;
    private Date lastMessage = new Date();

    public ExperienceMember(NBotUser user) {
        super(user);
    }

    public int getExperiences() {
        return experiences;
    }

    public int getLevels() {
        return levels;
    }

    public Date getLastMessage() {
        return lastMessage;
    }

    public void addMessage() {
        messages++;
    }

    public void addLevel() {
        levels++;
    }

    public void addExperience(int experience) {
        this.experiences += experience;
    }

    public void removeExperience(int experience) {
        this.experiences -= experience;
    }

    @Override
    public String toString() {
        return String.format("ID: %s / Experience: %s / Level: %s / Message: %s / LastMessage: %s", getUser().getUser().getId(), experiences, levels, messages, lastMessage);
    }

    @Override
    public void load() {

        String userId = getUser().getUser().getId();
        DatabasePlugin databasePlugin = (DatabasePlugin) NBot.get().getPluginManager().getPlugin(DatabasePlugin.class).get();

        try (Connection connection = databasePlugin.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM experience WHERE discord_id = ?");
            statement.setString(1, userId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                this.experiences = resultSet.getInt("experience");
                this.levels = resultSet.getInt("level");
                this.messages = resultSet.getInt("messages_count");
                this.lastMessage = resultSet.getTimestamp("last_message");
            }
        } catch (SQLException e) {
            databasePlugin.getLogger().error("Can't get experience from database (" + userId + ")");
            e.printStackTrace();
        }
    }

    @Override
    public void save() {

        String userId = getUser().getUser().getId();
        DatabasePlugin databasePlugin = (DatabasePlugin) NBot.get().getPluginManager().getPlugin(DatabasePlugin.class).get();

        try (Connection connection = databasePlugin.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO experience(`discord_id`, `experience`, `level`, `messages_count`, `last_message`) " +
                            "VALUES (?, ?, ?, ?, NOW()) ON DUPLICATE KEY UPDATE " +
                            "experience = VALUES(experience), level = VALUES(level), messages_count = VALUES(messages_count), last_message = VALUES(last_message)");

            statement.setString(1, userId);
            statement.setInt(2, this.experiences);
            statement.setInt(3, this.levels);
            statement.setInt(4, this.messages);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static int levelToExp(int level) {
        return 5 * level * level + 50 * level + 100;
    }
}