package com.frontier.logic;

import com.frontier.model.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class TelegramTest {
    @Test void completedWorkReportsActualRewardsAndFinds() {
        GameSession game = new GameSession(new Random(1)); Job job = game.jobsAt(Location.PINE_FOREST).getFirst();
        int money = game.state().player().money(), xp = game.state().player().xp();
        game.work(job, WorkDuration.SHORT);
        Telegram report = game.state().mailbox().messages().getFirst();
        assertEquals(job.name(), report.jobName()); assertEquals(job.location(), report.location());
        assertEquals(600, report.durationSeconds()); assertEquals(game.state().player().money() - money, report.money());
        assertEquals(game.state().player().xp() - xp, report.xp()); assertEquals(game.state().time().value(), report.dateTime());
        assertFalse(report.read()); assertEquals(1, game.state().mailbox().unreadCount());
        assertEquals(report.found() == null ? 0 : 1, game.state().inventory().totalCount());
        if (report.found() != null) assertEquals(1, game.state().inventory().count(report.found()));
    }
    @Test void actualFindIsRecordedInBothInventoryAndTelegram() {
        GameSession game = new GameSession(new Random(1) {
            @Override public int nextInt(int bound) { return 0; }
        });
        game.work(game.jobsAt(Location.PINE_FOREST).getFirst());
        assertEquals(Item.WOOD, game.state().mailbox().messages().getFirst().found());
        assertEquals(1, game.state().inventory().count(Item.WOOD));
    }
    @Test void timerOnlySendsOnceAtCompletion() {
        Instant now = Instant.parse("2026-10-07T12:00:00Z");
        GameSession game = new GameSession(new Random(1), Clock.fixed(now, ZoneOffset.UTC));
        game.startWork(game.jobsAt(Location.PINE_FOREST).getFirst(), WorkDuration.QUICK); game.updateWork();
        assertEquals(0, game.state().mailbox().size());
        GameSession completed = new GameSession(new Random(1), Clock.fixed(now.plusSeconds(15), ZoneOffset.UTC)); completed.load(game.state());
        completed.updateWork(); completed.updateWork(); assertEquals(1, completed.state().mailbox().size());
        assertEquals(15, completed.state().mailbox().messages().getFirst().durationSeconds());
    }
    @Test void travelSleepAndRejectedWorkDoNotSendTelegrams() {
        GameSession game = new GameSession(); game.work(); game.sleep(); game.travel(Location.PINE_FOREST); game.sleep();
        game.state().player().spendStamina(71); game.work(); assertEquals(0, game.state().mailbox().size());
    }
    @Test void mailboxMarksReadAndNewGameClearsHistory() {
        GameSession game = new GameSession(); game.work(game.jobsAt(Location.PINE_FOREST).getFirst());
        Mailbox mailbox = game.state().mailbox(); int revision = mailbox.revision();
        assertTrue(mailbox.read(0).read()); assertEquals(0, mailbox.unreadCount()); assertTrue(mailbox.revision() > revision);
        revision = mailbox.revision(); mailbox.read(0); assertEquals(revision, mailbox.revision());
        assertThrows(UnsupportedOperationException.class, () -> mailbox.messages().clear());
        game.newGame(); assertEquals(0, game.state().mailbox().size());
    }
    @Test void mailboxKeepsLatestHundredMessages() {
        Mailbox mailbox = new Mailbox();
        for (int i = 0; i <= Mailbox.MAX_MESSAGES; i++)
            mailbox.add(new Telegram("Klus " + i, Location.PINE_FOREST, 15, 1, 1, null, GameTime.START.plusSeconds(i), false));
        assertEquals(100, mailbox.size()); assertEquals("Klus 1", mailbox.messages().getFirst().jobName());
        assertEquals("Klus 100", mailbox.messages().getLast().jobName()); assertEquals(100, mailbox.unreadCount());
    }
}
