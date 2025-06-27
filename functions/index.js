const { onSchedule } = require("firebase-functions/v2/scheduler");
const { initializeApp } = require("firebase-admin/app");
const { getDatabase } = require("firebase-admin/database");

initializeApp();

exports.cleanupOldMessages = onSchedule(
  {
    schedule: "every 1 minutes",
    region: "asia-southeast1", // 💡 this deploys the function in correct region
  },
  async (event) => {
    const db = getDatabase(undefined, "https://lofigram-df368-default-rtdb.asia-southeast1.firebasedatabase.app"); // ✅ explicitly set your DB URL
    const ref = db.ref("global_chat");

    const snapshot = await ref.orderByChild("time").get();

    const allMessages = [];
    snapshot.forEach((child) => {
      allMessages.push({ key: child.key, time: child.val().time });
    });

    const total = allMessages.length;
    if (total > 50) {
      const messagesToDelete = allMessages
        .sort((a, b) => a.time - b.time)
        .slice(0, total - 50);

      const updates = {};
      messagesToDelete.forEach((msg) => {
        updates[msg.key] = null;
      });

      console.log("🔍 Total messages:", total);
      console.log("🗑️  Will delete keys:", messagesToDelete.map((m) => m.key));

      await ref.update(updates);
      console.log(`✅ Deleted ${messagesToDelete.length} old messages.`);
    } else {
      console.log("✅ No old messages to delete.");
    }
  }
);
