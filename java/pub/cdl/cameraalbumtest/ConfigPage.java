package pub.cdl.cameraalbumtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import pub.cdl.cameraalbumtest.bean.VoiceConfig;

public class ConfigPage extends AppCompatActivity implements View.OnClickListener {
    private Button speaker;
    private Button volume;
    private Button speed;
    private Button pitch;
    final private Config config = new Config();
    private class Config{
        private String name;
        private String before;
        private String now;
        public Config() {
            name = "";
            before = "";
            now = "";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBefore() {
            return before;
        }

        public void setBefore(String before) {
            this.before = before;
        }

        public String getNow() {
            return now;
        }

        public void setNow(String now) {
            this.now = now;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_page);

        speaker = findViewById(R.id.speaker);
        volume = findViewById(R.id.volume);
        speed = findViewById(R.id.speed);
        pitch = findViewById(R.id.pitch);

        speaker.setOnClickListener(this);
        volume.setOnClickListener(this);
        speed.setOnClickListener(this);
        pitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfigPage.this);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (config.name) {
                    case "":
                        break;
                    case "speaker":
                        VoiceConfig.setSPEAKER(config.getNow());
                        break;
                    case "volume":
                        VoiceConfig.setVOLUME(config.getNow());
                        break;
                    case "speed":
                        VoiceConfig.setSPEED(config.getNow());
                        break;
                    case "pitch":
                        VoiceConfig.setPITCH(config.getNow());
                    default:
                        break;
                }
            }
        });

        builder.setNegativeButton("Cancel", (DialogInterface dialog, int which) -> {

        });

        int now;
        switch (v.getId()) {
            case R.id.speaker:
                config.setBefore(VoiceConfig.getSPEAKER());
                config.setName("speaker");
                now = Integer.valueOf(VoiceConfig.getSPEAKER());
                builder.setTitle("选择音色");
                final String[] speaker = {
                        "普通女声(默认)",
                        "普通男声",
                        "特别男声",
                        "情感男声",
                        "童声"
                };
                builder.setSingleChoiceItems(speaker, now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        config.setNow("" + which);
                    }
                });
                break;
            case R.id.volume:
                config.setBefore(VoiceConfig.getVOLUME());
                config.setName("volume");
                now = Integer.valueOf(config.getBefore());
                builder.setTitle("音量选择");
                final String[] volume = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9(默认)"};
                builder.setSingleChoiceItems(volume, now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        config.setNow("" + which);
                    }
                });
                break;
            case R.id.speed:
                config.setBefore(VoiceConfig.getSPEED());
                config.setName("speed");
                now = Integer.valueOf(config.getBefore());
                builder.setTitle("语速选择");
                final String[] speed = {"0", "1", "2", "3", "4", "5(默认)", "6", "7", "8", "9"};
                builder.setSingleChoiceItems(speed, now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        config.setNow("" + which);
                    }
                });
                break;
            case R.id.pitch:
                config.setBefore(VoiceConfig.getPITCH());
                config.setName("pitch");
                now = Integer.valueOf(config.getBefore());
                builder.setTitle("语调选择");
                final String[] pitch = {"0", "1", "2", "3", "4", "5(默认)", "6", "7", "8", "9"};
                builder.setSingleChoiceItems(pitch, now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        config.setNow("" + which);
                    }
                });
                break;
            default:
                break;
        }
        builder.show();
    }
}
