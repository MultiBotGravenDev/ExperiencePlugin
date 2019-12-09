package fr.gravendev.listeners;

import fr.gravendev.experience.ExperienceMember;
import fr.neutronstars.nbot.api.NBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public class ExperienceListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        if (member == null || member.getUser().isBot()) {
            return;
        }

        ExperienceMember memberExp = NBot.get().getUser(member.getUser()).get(ExperienceMember.class);
        if (memberExp.getLastMessage().getTime() + 60000 < System.currentTimeMillis()) {
            int xpEarned = ThreadLocalRandom.current().nextInt(15, 26);
            memberExp.addMessage();
            memberExp.addExperience(xpEarned);
            int requireToLevelUp = ExperienceMember.levelToExp(memberExp.getLevels());
            if (memberExp.getExperiences() > requireToLevelUp) {
                memberExp.addLevel();
                memberExp.removeExperience(requireToLevelUp);
            }
            memberExp.save();
        }
    }

}
