package fr.gravendev;

import fr.gravendev.commands.RankCommand;
import fr.gravendev.experience.ExperienceMember;
import fr.gravendev.listeners.ExperienceListener;
import fr.neutronstars.nbot.api.NBot;
import fr.neutronstars.nbot.api.event.Listener;
import fr.neutronstars.nbot.api.event.server.NBotServerStartedEvent;
import fr.neutronstars.nbot.api.event.server.NBotServerStartingEvent;
import fr.neutronstars.nbot.api.language.LanguageTypes;
import fr.neutronstars.nbot.api.plugin.NBotPlugin;

public class ExperiencePlugin extends NBotPlugin {

    public ExperiencePlugin() {
        super("experience_plugin", "Experience Plugin", "1.0", "", "Nolan");
    }

    @Listener
    public void onStarting(NBotServerStartingEvent event) {
        super.getLogger().info("Starting " + super.getName() + "...");
        super.registerLanguage(LanguageTypes.FR_FR, "/assets/language/");

        super.registerCommand("rank", "command.rank.description", new RankCommand(), super.getId() + ".command.rank");

        NBot.get().registerCustomUser(ExperienceMember.class);
    }

    @Listener
    public void onStarted(NBotServerStartedEvent event) {
        NBot.get().getShardManager().addEventListener(new ExperienceListener());
    }
}