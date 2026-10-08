> **مرجع للقراءة، مو مصدر للنسخ.** كود هالريبو ما التزم بالقيود المعمارية ب-١ لـ ب-٨ (`design.md` القسم ٢)، و`principles.md` بيمنع نقله لـ Third كما هو.

# third-proto — نماذج الموجة ٠

نماذج الجدوى الأربعة لمود [Third](https://github.com/abod1nd/third)، قبل ما يبلّش المشروع الحقيقي.
كل النتائج والقيود والقرارات موجودة بـ [`docs/design.md`](https://github.com/abod1nd/third/blob/main/docs/design.md) (النسخة ٥.٥). هالملف بيأشّر عليها بس.

## التشغيل

- Minecraft 1.21.11 · NeoForge 21.11.45 · ModDevGradle · JDK 21
- من جذر المشروع: `.\gradlew runClient`، أو run configuration الـ Client بـ IntelliJ
- عالم جديد، Creative، وصعوبة غير Peaceful

**قبل أي تجربة:**

- **W0-3 شغّال دايمًا.** أي عالم بينفتح فيه المود بيحط القطع، وبيحبس اللاعب بالجزيرة، وبيمنع السفر لأي بُعد تاني. يعني كل التجارب بتنعمل على الجزيرة.
- الكود موزّع على حزمتين: `com.abood.thirdproto` (W0-1 لـ W0-3) و`com.abood.third_proto` (W0-4). عدم التوحيد متروك عن قصد.
- `example_block` و`example_item` و`example_tab` و`Config.java` بقايا من قالب الـ MDK، وما إلهن علاقة بأي تجربة.
- مود Huge Structure Blocks مو مضمّن، وما بيلزم للتشغيل، لأنه القطع محفوظة جاهزة.

---

## W0-1 — سكن Echo

- **السؤال:** هل بقدر آخد سكن لاعب حقيقي وقت التشغيل وأركّبه على كيان مخصص؟
- **الحالة:** ✅ مُغلق
- **الكود:** `com.abood.thirdproto` ← `entity/EchoTestEntity`، `registry/ModEntities`، `client/ClientSetup`، `client/renderer/EchoTestRenderer` و`EchoTestRenderState` · السكنات بـ `assets/third_proto/textures/entity/`
- **تنبيه:** الكود الموجود هو آخر اختبار بالتجربة بس، يعني اختبار الـ dispatcher. كود تركيب السكنين ما انحفظ، وطريقه موثّق بـ design.md. الملف `friend_skin.png` مو مستعمل بالكود الحالي.
- **التشغيل:** `/summon third_proto:echo_test ~ ~ ~3`، وراقب بالكونسول سطر `GET_TEXTURE_LOCATION CALLED`
- **المرجع:** design.md القسم ٤ (الاختبار الموجود: ٤.٢ · التركيب: ٤.١ و٤.٣)

## W0-2 — الرمح والـ Riptide

- **السؤال:** هل بيقدر عنصر مخصص، بموديل وتكستشر خاصين، يعطي سلوك Riptide كامل؟
- **الحالة:** ✅ مُغلق
- **الكود:** `com.abood.thirdproto.w02` ← `ProtoSpearItem`، `W02Items` · الموارد: `items/proto_spear.json`، `models/item/proto_spear.json`، `textures/item/proto_spear.png`
- **التشغيل:**
  - الرمح: `/give @s third_proto:proto_spear[enchantments={"minecraft:riptide":3}]`
  - Riptide: روح عالمنصة B (`/tp @s 44 66 -40`)، حط ماء (`/give @s water_bucket`)، وقف فيه، واشحن بالكبسة اليمين. التجربة الأصلية انعملت قبل W0-3، بالمطر والبحر، والمطر ما انجرّب على الجزيرة.
  - الطعنة: استدعي ٣ husk على خط واحد قدامك، واضرب كبسة شمال وحدة.
- **المرجع:** design.md القسم ٥.٤ (الطعنة: ٥.٤.٥)

## W0-3 — البُعد والقطع

- **السؤال:** هل بقدر أسجّل بُعد فراغ، وأحط فيه قطع بنية بإحداثيات ثابتة، وأنقل اللاعب إجباريًا إليه بلا رجوع؟
- **الحالة:** ✅ مُغلق
- **الكود:** `com.abood.thirdproto.w03` ← `IslandConstants` (كل الإحداثيات)، `IslandEvents`، `IslandPlacer` · الداتا بـ `data/third_proto/`: `dimension/island.json`، `dimension_type/island.json`، `structure/piece_a|b|c.nbt`، `worldgen/biome/red_sky.json`
- **التشغيل:**
  - الوضع والنقل: تلقائي مع أول دخول لعالم جديد. كل لوغ التجربة بيبلّش بـ `[W0-3]`
  - منع السفر: `/execute in minecraft:overworld run tp @s 0 100 0`
  - الإحياء: `/kill @s`
  - السما الحمرا: `/fillbiome ~-10 ~-10 ~-10 ~10 ~10 ~10 third_proto:red_sky`
  - القطعة الكبيرة C: `/tp @s -68 66 32`
- **المرجع:** design.md القسم ٥.٥

## W0-4 — نفق الرؤية

- **السؤال:** هل بقدر أعمل نفق رؤية بلا mod تظليل خارجي، مستقل لكل لاعب؟
- **الحالة:** ✅ مُغلق
- **الكود:** `com.abood.third_proto.w04` ← `W04Registry` (التأثير) · `w04.client` ← `TunnelVisionLayer` (نقطة الدخول)، `ShaderTunnelRenderer`، `MaskTunnelRenderer`، والباقي · الـ shader: `assets/third_proto/shaders/post/tunnel_vision.fsh`
- **التشغيل:**
  - روح عالمنصة B، بعيد عن الغرفة: الهدف هو أقرب `Mob` ضمن ٦٤ بلوك، والقطة لألو يلي بالغرفة كمان `Mob`
  - `/summon husk ~ ~ ~8`
  - `/effect give @s third_proto:tunnel_vision infinite` و`/effect give @s minecraft:slowness infinite 1`
  - للإيقاف: `/effect clear @s`
  - لتجرّب القناع بدل الـ shader: غيّر `USE_SHADER` لـ `false` بـ `TunnelVisionLayer`
- **المرجع:** design.md القسم ٥.٦
