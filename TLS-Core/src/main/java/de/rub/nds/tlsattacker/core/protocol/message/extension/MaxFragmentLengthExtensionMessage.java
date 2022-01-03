/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.message.extension;

import de.rub.nds.modifiablevariable.ModifiableVariableFactory;
import de.rub.nds.modifiablevariable.ModifiableVariableProperty;
import de.rub.nds.modifiablevariable.bytearray.ModifiableByteArray;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.constants.MaxFragmentLength;
import de.rub.nds.tlsattacker.core.protocol.handler.extension.MaxFragmentLengthExtensionHandler;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.MaxFragmentLengthExtensionParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.MaxFragmentLengthExtensionPreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.MaxFragmentLengthExtensionSerializer;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.io.InputStream;

/**
 * Maximum Fragment Length Extension described in rfc3546
 */
public class MaxFragmentLengthExtensionMessage extends ExtensionMessage<MaxFragmentLengthExtensionMessage> {

    private MaxFragmentLength maxFragmentLengthConfig;

    /**
     * Maximum fragment length value described in rfc3546
     */
    @ModifiableVariableProperty(type = ModifiableVariableProperty.Type.TLS_CONSTANT)
    private ModifiableByteArray maxFragmentLength;

    public MaxFragmentLengthExtensionMessage() {
        super(ExtensionType.MAX_FRAGMENT_LENGTH);
    }

    public MaxFragmentLengthExtensionMessage(Config config) {
        super(ExtensionType.MAX_FRAGMENT_LENGTH);
    }

    public ModifiableByteArray getMaxFragmentLength() {
        return maxFragmentLength;
    }

    public void setMaxFragmentLength(ModifiableByteArray maxFragmentLength) {
        this.maxFragmentLength = maxFragmentLength;
    }

    public void setMaxFragmentLength(byte[] maxFragmentLength) {
        this.maxFragmentLength = ModifiableVariableFactory.safelySetValue(this.maxFragmentLength, maxFragmentLength);
    }

    @Override
    public MaxFragmentLengthExtensionParser getParser(TlsContext tlsContext, InputStream stream) {
        return new MaxFragmentLengthExtensionParser(stream, tlsContext.getConfig());
    }

    @Override
    public MaxFragmentLengthExtensionPreparator getPreparator(TlsContext tlsContext) {
        return new MaxFragmentLengthExtensionPreparator(tlsContext.getChooser(), this, getSerializer(tlsContext));
    }

    @Override
    public MaxFragmentLengthExtensionSerializer getSerializer(TlsContext tlsContext) {
        return new MaxFragmentLengthExtensionSerializer(this);
    }

    @Override
    public MaxFragmentLengthExtensionHandler getHandler(TlsContext tlsContext) {
        return new MaxFragmentLengthExtensionHandler(tlsContext);
    }
}
