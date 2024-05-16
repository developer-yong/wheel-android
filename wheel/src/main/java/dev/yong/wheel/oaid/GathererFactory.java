package dev.yong.wheel.oaid;

import android.app.Application;
import android.content.Context;

import dev.yong.wheel.oaid.impl.AsusGathererImpl;
import dev.yong.wheel.oaid.impl.CoolpadGathererImpl;
import dev.yong.wheel.oaid.impl.CooseaGathererImpl;
import dev.yong.wheel.oaid.impl.DefaultGathererImpl;
import dev.yong.wheel.oaid.impl.FreemeGathererImpl;
import dev.yong.wheel.oaid.impl.GmsGathererImpl;
import dev.yong.wheel.oaid.impl.HuaweiGathererImpl;
import dev.yong.wheel.oaid.impl.LenovoGathererImpl;
import dev.yong.wheel.oaid.impl.MeizuGathererImpl;
import dev.yong.wheel.oaid.impl.MsaGathererImpl;
import dev.yong.wheel.oaid.impl.NubiaGathererImpl;
import dev.yong.wheel.oaid.impl.OppoExtGathererImpl;
import dev.yong.wheel.oaid.impl.OppoGathererImpl;
import dev.yong.wheel.oaid.impl.SamsungGathererImpl;
import dev.yong.wheel.oaid.impl.VivoGathererImpl;
import dev.yong.wheel.oaid.impl.XiaomiGathererImpl;

/**
 * OAID采集实现工厂类
 *
 * @author Developer丶永（dev_yong@yeah.net）
 * @date 2024/2/21
 */
public final class GathererFactory {

    private static IGatherer sGatherer;

    private GathererFactory() {
        super();
    }

    public static IGatherer create(Context context) {
        if (context != null && !(context instanceof Application)) {
            // activity退到桌面在进入app无法绑定服务
            context = context.getApplicationContext();
        }
        if (sGatherer != null) {
            return sGatherer;
        }
        // 优先尝试各厂商自家提供的接口
        sGatherer = createManufacturerImpl(context);
        if (sGatherer != null && sGatherer.isSupported()) {
            OAIDLog.print("Manufacturer interface has been found: " + sGatherer.getClass().getName());
            return sGatherer;
        }
        // 再尝试移动安全联盟及谷歌服务框架提供的接口
        sGatherer = createUniversalImpl(context);
        return sGatherer;
    }

    private static IGatherer createManufacturerImpl(Context context) {
        if (ROM.isLenovo() || ROM.isMotolora()) {
            return new LenovoGathererImpl(context);
        }
        if (ROM.isMeizu()) {
            return new MeizuGathererImpl(context);
        }
        if (ROM.isNubia()) {
            return new NubiaGathererImpl(context);
        }
        if (ROM.isXiaomi() || ROM.isMiui() || ROM.isBlackShark()) {
            return new XiaomiGathererImpl(context);
        }
        if (ROM.isSamsung()) {
            return new SamsungGathererImpl(context);
        }
        if (ROM.isVivo()) {
            return new VivoGathererImpl(context);
        }
        if (ROM.isASUS()) {
            return new AsusGathererImpl(context);
        }
//        if (ROM.isHonor()) {
//            HonorGathererImpl honor = new HonorGathererImpl(context);
//            if (honor.isSupported()) {
//                // 支持的话（Magic UI 4.0,5.0,6.0及MagicOS 7.0或以上）直接使用荣耀的实现，否则尝试华为的实现
//                return honor;
//            }
//        }
        if (ROM.isHuawei() || ROM.isEmui()) {
            return new HuaweiGathererImpl(context);
        }
        if (ROM.isOppo() || ROM.isOnePlus()) {
            OppoGathererImpl oppo = new OppoGathererImpl(context);
            if (oppo.isSupported()) {
                return oppo;
            }
            return new OppoExtGathererImpl(context);
        }
        if (ROM.isCoolpad(context)) {
            return new CoolpadGathererImpl(context);
        }
        if (ROM.isCoosea()) {
            return new CooseaGathererImpl(context);
        }
        if (ROM.isFreeme()) {
            return new FreemeGathererImpl(context);
        }
        return null;
    }

    private static IGatherer createUniversalImpl(Context context) {
        // 若各厂商自家没有提供接口，则优先尝试移动安全联盟的接口
        IGatherer gatherer = new MsaGathererImpl(context);
        if (gatherer.isSupported()) {
            OAIDLog.print("Mobile Security Alliance has been found: " + gatherer.getClass().getName());
            return gatherer;
        }
        // 若不支持移动安全联盟的接口，则尝试谷歌服务框架的接口
        gatherer = new GmsGathererImpl(context);
        if (gatherer.isSupported()) {
            OAIDLog.print("Google Play Service has been found: " + gatherer.getClass().getName());
            return gatherer;
        }
        // 默认不支持
        gatherer = new DefaultGathererImpl();
        OAIDLog.print("OAID/AAID was not supported: " + gatherer.getClass().getName());
        return gatherer;
    }

}
