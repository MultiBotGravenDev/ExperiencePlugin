package fr.gravendev.commands;

import fr.gravendev.experience.ExperienceMember;
import fr.gravendev.images.ImageBuilder;
import fr.neutronstars.nbot.api.NBot;
import fr.neutronstars.nbot.api.command.CommandExecutor;
import fr.neutronstars.nbot.api.entity.NBotUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class RankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(NBotUser nBotUser, Message message, String... args) {
        Member member = message.getMember();
        if (member == null) {
            return false;
        }

        List<Member> mentionedMembers = message.getMentionedMembers();
        if (mentionedMembers.size() > 0) {
            member = mentionedMembers.get(0);
        }

        User user = member.getUser();
        if (user.isBot()) {
            return false;
        }

        ExperienceMember memberExperience = NBot.get().getUser(user).get(ExperienceMember.class);
        int experiences = memberExperience.getExperiences();
        int levels = memberExperience.getLevels();
        int expToLevelUp = ExperienceMember.levelToExp(levels);


        BufferedImage image = new BufferedImage(950, 300, BufferedImage.TYPE_INT_RGB);
        ImageBuilder builder = new ImageBuilder(image);

        try {
            builder.drawImage(new URL(user.getAvatarUrl()));
        } catch (IOException e) {
            NBot.get().getLogger().error("Impossible d'afficher l'avatar de " + user.getAsTag() + " !");
            e.printStackTrace();
        }

        Color color = getColor(member.getRoles());
        builder.drawProgress(color, experiences, expToLevelUp);

        builder.drawString(String.format("%d/%d", experiences, expToLevelUp), color, 730, 190, 25);
        String name = getName(user);
        builder.drawString(name + "#" + user.getDiscriminator(), color, 280, 185, 35);
        builder.drawString(String.format("Niveau %d", levels), Color.WHITE, levels >= 100 ? 680 : 710, 90, 35);

        try {
            message.getChannel().sendFile(builder.toInputStream(), "card.png").queue();
        } catch (IOException e) {
            NBot.get().getLogger().error("Impossible d'envoyer la carte de niveau de " + user.getAsTag() + " !");
            e.printStackTrace();
        }
        return false;
    }

    private String getName(User user) {
        String name = user.getName();
        if (name.length() >= 11) {
            name = name.substring(0, 11);
        }
        return name;
    }

    private Color getColor(List<Role> roles) {
        if (roles.size() == 0) {
            return Color.WHITE;
        }
        Optional<Role> optionalRole = roles.stream()
                .filter(c -> c != null && c.getColor() != Color.BLACK)
                .findFirst();
        return optionalRole.isPresent() ? optionalRole.get().getColor() : Color.WHITE;
    }

}
